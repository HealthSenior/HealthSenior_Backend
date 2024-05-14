package out4ider.healthsenior.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.domain.UserFcmToken;
import out4ider.healthsenior.repository.SeniorUserRepository;
import out4ider.healthsenior.repository.UserFcmRepository;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeniorUserService {
    private final SeniorUserRepository seniorUserRepository;
    private final UserFcmRepository userFcmRepository;

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

    public String getFcmToken(String oauth2Id) throws Exception {
        Optional<UserFcmToken> byId = userFcmRepository.findById(oauth2Id);
        if (byId.isEmpty()){
            log.error("no fcmToken of user");
            throw new Exception();
        }
        else{
            return byId.get().getFcmToken();
        }
    }
}
