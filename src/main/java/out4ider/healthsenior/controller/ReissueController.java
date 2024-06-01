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
import out4ider.healthsenior.repository.RefreshTokenRepository;
import out4ider.healthsenior.service.RefreshTokenService;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
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

        Boolean isExist = refreshTokenRepository.existsByRefreshToken(refresh);
        if (!isExist) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }
        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);
        refreshTokenRepository.deleteByRefreshToken(refresh);
        String refreshToken = jwtUtil.createToken("refresh", username, Role.USER, 604800000L);
        response.setHeader("Refresh", refreshToken);
        refreshTokenService.addRefreshToken(username, refreshToken, 86400000L);
        String newAccess = jwtUtil.createToken("access", username, Role.USER, 86400000L);
        response.setHeader("Authorization", "Bearer " + newAccess);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
