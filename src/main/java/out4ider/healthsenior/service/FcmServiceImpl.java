package out4ider.healthsenior.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import out4ider.healthsenior.domain.UserFcmToken;
import out4ider.healthsenior.dto.FcmMessageDto;
import out4ider.healthsenior.dto.FcmSendDto;
import out4ider.healthsenior.repository.UserFcmRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FcmServiceImpl implements FcmService{
    private final UserFcmRepository userFcmRepository;
    private final RedisService redisService;

    @Override
    public int sendMessageTo(FcmSendDto fcmSendDto, String fcmToken) throws IOException {
        String message = makeMessage(fcmSendDto, fcmToken);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Authorization", "Bearer " + getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>(message, httpHeaders);
        String API_URL = "https://fcm.googleapis.com/v1/projects/healthsenior/messages:send";
        ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
        return response.getStatusCode() == HttpStatus.OK ? 1 : 0;
    }

    public String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/fcm-key.json";
        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        googleCredentials.refreshIfExpired();
        AccessToken accessToken = googleCredentials.getAccessToken();
        String tokenValue = accessToken.getTokenValue();
        return tokenValue;
    }

    private String makeMessage(FcmSendDto fcmSendDto,String fcmToken) throws JsonProcessingException {

        ObjectMapper om = new ObjectMapper();
        FcmMessageDto fcmMessageDto = FcmMessageDto.builder()
                .message(FcmMessageDto.Message.builder()
                        .token(fcmToken)
                        .notification(FcmMessageDto.Notification.builder()
                                .title(fcmSendDto.getTitle())
                                .body(fcmSendDto.getBody())
                                .build()
                        ).build()).validateOnly(false).build();

        return om.writeValueAsString(fcmMessageDto);
    }

    @Override
    public void saveOfUpdateFcmToken(String oauth2Id,String fcm_token){
        UserFcmToken userFcm;
        Optional<UserFcmToken> fcmById = userFcmRepository.findById(oauth2Id);
        if (fcmById.isEmpty()){
            userFcm = new UserFcmToken(oauth2Id,fcm_token);
        }
        else{
            userFcm = fcmById.get();
            if (!userFcm.getFcmToken().equals(fcm_token)) {
                userFcm.updateFcmToken(fcm_token);
                redisService.updateUserFcmToken(oauth2Id,fcm_token);
            }
        }
        userFcmRepository.save(userFcm);
    }

    @Override
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
