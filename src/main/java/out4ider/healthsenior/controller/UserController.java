package out4ider.healthsenior.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.dto.UserDto;
import out4ider.healthsenior.jwt.JWTUtil;
import out4ider.healthsenior.service.FcmService;
import out4ider.healthsenior.service.RedisService;
import out4ider.healthsenior.service.SeniorUserService;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@Slf4j
public class UserController {

    private final SeniorUserService seniorUserService;
    private final JWTUtil jwtUtil;
    private final FcmService fcmService;
    private final RedisService redisService;

    @PostMapping("/login")
    public String login(@RequestBody UserDto userDto, @RequestHeader Map<String,String> headers, HttpServletResponse response) {
        SeniorUser seniorUser = seniorUserService.saveSeniorUser(userDto);
        String oauth2Id = seniorUser.getOauth2Id();
        String fcm_token = headers.get("fcm-token");
        fcmService.saveOfUpdateFcmToken(oauth2Id,fcm_token);

        response.setHeader("Authorization", "Bearer "+jwtUtil.createToken("access", oauth2Id, seniorUser.getRole(), 86400000L));
        String refreshToken = jwtUtil.createToken("refresh", oauth2Id, seniorUser.getRole(), 604800000L);
        response.setHeader("Refresh", refreshToken);
        redisService.putRefreshToken(oauth2Id, refreshToken, 604800000L);

        return "Login success";
    }
}
