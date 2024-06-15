package out4ider.healthsenior.controller;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import out4ider.healthsenior.enums.Role;
import out4ider.healthsenior.jwt.JWTUtil;
import out4ider.healthsenior.service.RedisService;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RedisService redisService;

    @PostMapping("/reissue")
    public ResponseEntity<?>reissue(HttpServletRequest request, HttpServletResponse response) {
        String refresh = null;
        refresh = request.getHeader("Refresh");
        if(refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);
        //유저가 가지고 있는 refresh가 맞는지 확인
        if(!redisService.getRefreshToken(username).equals(refresh)){
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }
        redisService.deleteRefreshToken(username);
        String refreshToken = jwtUtil.createToken("refresh", username, Role.USER, 604800000L);
        response.setHeader("Refresh", refreshToken);
        redisService.putRefreshToken(username, refreshToken, 86400000L);
        String newAccess = jwtUtil.createToken("access", username, Role.USER, 86400000L);
        response.setHeader("Authorization", "Bearer " + newAccess);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
