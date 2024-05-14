package out4ider.healthsenior.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.dto.UserDto;
import out4ider.healthsenior.enums.Role;
import out4ider.healthsenior.jwt.JWTUtil;
import out4ider.healthsenior.service.RefreshTokenService;
import out4ider.healthsenior.service.SeniorUserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@Slf4j
public class UserController {

    private final SeniorUserService seniorUserService;
    private final JWTUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public String login(@RequestBody UserDto userDto, HttpServletResponse response) {
        String registrationId = userDto.getRegistrationId();
        String oauth2Id = registrationId + userDto.getUserId();
        boolean isMale = false;
        if (userDto.getGender().equals("m")) {
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
        response.setHeader("Authorization", "Bearer "+jwtUtil.createToken("access", oauth2Id, seniorUser.getRole(), 600000L));
        String refreshToken = jwtUtil.createToken("refresh", oauth2Id, seniorUser.getRole(), 86400000L);
        response.setHeader("Refresh", refreshToken);
        refreshTokenService.addRefreshToken(oauth2Id, refreshToken, 86400000L);
        return registrationId+"Login success";
    }
}
