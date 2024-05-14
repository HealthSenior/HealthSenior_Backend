package out4ider.healthsenior.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import out4ider.healthsenior.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    public boolean existsByRefreshToken(String refreshToken);
    public void deleteByRefreshToken(String refreshToken);
}
