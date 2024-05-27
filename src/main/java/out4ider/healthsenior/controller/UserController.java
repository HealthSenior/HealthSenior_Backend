package out4ider.healthsenior.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.domain.UserFcmToken;
import out4ider.healthsenior.dto.TokenLoginResponseDto;
import out4ider.healthsenior.dto.UserDto;
import out4ider.healthsenior.enums.Role;
import out4ider.healthsenior.jwt.JWTUtil;
import out4ider.healthsenior.repository.UserFcmRepository;
import out4ider.healthsenior.service.RedisService;
import out4ider.healthsenior.service.SeniorUserService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@Slf4j
public class UserController {

    private final SeniorUserService seniorUserService;
    private final JWTUtil jwtUtil;
    private final UserFcmRepository userFcmRepository;
    private final RedisService redisService;

    @PostMapping("/login")
    public String login(@RequestBody UserDto userDto, @RequestHeader Map<String,String> headers, HttpServletResponse response) {
        String fcm_token = headers.get("fcm-token");
        log.info("fcm-token = {}",fcm_token);
        String registrationId = userDto.getRegistrationId();
        String oauth2Id = registrationId + userDto.getUserId();
        UserFcmToken userFcm;
        Optional<UserFcmToken> fcmById = userFcmRepository.findById(oauth2Id);
        if (fcmById.isEmpty()){
            userFcm = new UserFcmToken(oauth2Id,fcm_token);
        }
        else{
            userFcm = fcmById.get();
            if (!userFcm.getFcmToken().equals(fcm_token)) {
                userFcm.updateFcmToken(fcm_token);
                redisService.updateUserFcmToken(oauth2Id,fcm_token);
            }
        }
        userFcmRepository.save(userFcm);
        boolean isMale = false;
        if (userDto.getGender()==null || userDto.getGender().equals("m")) {
            isMale = true;
        }
        Optional<SeniorUser> byOauth2Id = seniorUserService.findByOauth2Id(oauth2Id);
        SeniorUser seniorUser;
        if (byOauth2Id.isEmpty()) {
            seniorUser = SeniorUser.builder()
                    .userAge(LocalDateTime.now().getYear() - Integer.parseInt(userDto.getBirthYear()) + 1)
                    .userName(userDto.getName())
                    .email(userDto.getEmail()).role(Role.USER).oauth2Id(oauth2Id)
                    .communityChatRelation(new ArrayList<>())
                    .articleList(new ArrayList<>())
                    .commentList(new ArrayList<>())
                    .likeUserRelations(new ArrayList<>())
                    .isMale(isMale).build();
            seniorUserService.saveSeniorUser(seniorUser);
        } else {
            seniorUser = byOauth2Id.get();
        }
        response.setHeader("Authorization", "Bearer "+jwtUtil.createToken(oauth2Id, seniorUser.getRole(), 600*600*600L));
        return registrationId+"Login success";
    }

    @GetMapping("/token-login")
    public TokenLoginResponseDto tokenLogin(Principal principal) throws Exception {
        String name = principal.getName();
        Optional<SeniorUser> byOauth2Id = seniorUserService.findByOauth2Id(name);
        if (byOauth2Id.isEmpty()) throw new Exception();
        SeniorUser seniorUser = byOauth2Id.get();
        TokenLoginResponseDto requestDto = TokenLoginResponseDto.builder()
                .gender(seniorUser.isMale())
                .age(seniorUser.getUserAge())
                .email(seniorUser.getEmail())
                .name(seniorUser.getUserName())
                .build();
        return requestDto;
    }
}
