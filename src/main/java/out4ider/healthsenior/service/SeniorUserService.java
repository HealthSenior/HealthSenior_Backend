package out4ider.healthsenior.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.repository.SeniorUserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SeniorUserService {
    private final SeniorUserRepository seniorUserRepository;

    public Optional<SeniorUser> findByEmail(String email){
        Optional<SeniorUser> byEmail = seniorUserRepository.findByEmail(email);
        return byEmail;
    }

    public Optional<SeniorUser> findByOauth2Id(String oauth2Id){
        Optional<SeniorUser> byOauthId = seniorUserRepository.findByOauth2Id(oauth2Id);
        return byOauthId;
    }

    public void saveSeniorUser(SeniorUser seniorUser){
        seniorUserRepository.save(seniorUser);
    }
}
