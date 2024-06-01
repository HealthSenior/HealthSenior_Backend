package out4ider.healthsenior.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import out4ider.healthsenior.dto.FcmMessageDto;
import out4ider.healthsenior.dto.FcmSendDto;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class FcmServiceImpl implements FcmService{

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
}
