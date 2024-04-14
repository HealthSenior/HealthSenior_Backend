package out4ider.healthsenior.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
public class JWTUtil {
    private final SecretKey secretKey;

    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    //검증에 필요한 함수 정의 더 추가해야함
    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }
    
    //payload에 넣을 정보 더 넣기
    public String createToken(String email, String role) {
        return Jwts.builder()
                .claim("email", email)
                .claim("role", role)
                //만료 기한 추가
                .signWith(secretKey)
                .compact();
    }

}
