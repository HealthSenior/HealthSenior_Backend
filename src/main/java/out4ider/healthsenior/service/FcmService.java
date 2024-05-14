package out4ider.healthsenior.service;

import org.springframework.stereotype.Service;
import out4ider.healthsenior.dto.FcmSendDto;

import java.io.IOException;

@Service
public interface FcmService {
    int sendMessageTo(FcmSendDto fcmSendDto,String fcmToken) throws IOException;
}
