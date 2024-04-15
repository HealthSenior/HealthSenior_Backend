package out4ider.healthsenior.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.dto.UserDto;
import out4ider.healthsenior.enums.Role;
import out4ider.healthsenior.jwt.JWTUtil;
import out4ider.healthsenior.service.SeniorUserService;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final SeniorUserService seniorUserService;
    private final JWTUtil jwtUtil;

    @GetMapping("/kakaoLogin")
    public String kakaoLogin(@RequestBody UserDto userDto) {
        String oauth2Id = "kakao" + userDto.getUserId();
        boolean isMale = false;
        if (userDto.getGender().equals("male")) {
            isMale = true;
        }
        Optional<SeniorUser> byOauth2Id = seniorUserService.findByOauth2Id(oauth2Id);
        SeniorUser seniorUser;
        if (byOauth2Id.isEmpty()) {
            seniorUser = SeniorUser.builder()
                    .userAge(LocalDateTime.now().getYear() - Integer.parseInt(userDto.getBirthYear()) + 1)
                    .userName(userDto.getName())
                    .email(userDto.getEmail()).role(Role.USER).oauth2Id(oauth2Id)
                    .isMale(isMale).build();
            seniorUserService.saveSeniorUser(seniorUser);
        } else {
            seniorUser = byOauth2Id.get();
            //업데이트 해줘야함
        }
        return jwtUtil.createToken(oauth2Id, seniorUser.getRole(), 60*60*60L);
    }
}
