package out4ider.healthsenior.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import out4ider.healthsenior.domain.RefreshToken;
import out4ider.healthsenior.repository.RefreshTokenRepository;

import java.util.Date;


@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void addRefreshToken(String oauth2Id, String refreshToken, Long expiration) {
        RefreshToken token = RefreshToken.builder()
                .oauth2Id(oauth2Id)
                .refreshToken(refreshToken)
                .expiration(new Date(System.currentTimeMillis()+expiration).toString())
                .build();
        refreshTokenRepository.save(token);
    }

    public boolean existRefreshToken(String refreshToken) {
        return refreshTokenRepository.existsByRefreshToken(refreshToken);
    }

    public void deleteRefreshToken(String refreshToken) {
        refreshTokenRepository.deleteByRefreshToken(refreshToken);
    }
}
