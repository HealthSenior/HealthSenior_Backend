package out4ider.healthsenior.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import out4ider.healthsenior.domain.ChatMessage;
import out4ider.healthsenior.dto.ChatRequest;
import out4ider.healthsenior.dto.FcmSendDto;
import out4ider.healthsenior.jwt.JWTUtil;
import out4ider.healthsenior.service.AsyncService;
import out4ider.healthsenior.service.ChatMessageService;
import out4ider.healthsenior.service.FcmService;
import out4ider.healthsenior.service.RedisService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class ChatPreHandle implements ChannelInterceptor {
    private final RedisService redisService;
    private final JWTUtil jwtUtil;
    private final FcmService fcmService;
    private final ChatMessageService chatMessageService;
    private final Map<String, WebSocketSession> webSocketSessionMap;
    private final AsyncService asyncService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        ChatRequest chatRequest = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String sessionId  = accessor.getSessionId();
        if (StompCommand.CONNECT == accessor.getCommand()) { // websocket 연결요청
            String jwtToken = accessor.getFirstNativeHeader("authorization");
            String roomNumber = accessor.getFirstNativeHeader("roomnumber");

            // Header의 jwt token 검증 해야함!
            // user가 roomnumber의 채팅방에 속하는지 검사해야함!

            String token = jwtToken.substring(7);
            String oauth2Id = jwtUtil.getUsername(token);

            log.info("ID : {} ", oauth2Id);
            redisService.enterChatRoom(sessionId,roomNumber,oauth2Id);
        }
        else if (StompCommand.SEND == accessor.getCommand()){
            //test
            long beforeTime = System.currentTimeMillis();
            String roomNumber = accessor.getFirstNativeHeader("roomnumber");
            Object payload = message.getPayload();
            if (payload instanceof byte[]) {
                String content = new String((byte[]) payload, StandardCharsets.UTF_8);
                try {
                     chatRequest = objectMapper.readValue(content, ChatRequest.class);
                } catch (JsonProcessingException e) {
                    log.error("바꿀수없음");
                    throw new RuntimeException(e);
                }
            } else {
                log.info("Message payload is not a byte array");
            }
            //메시지 시간을 클라이언트에서 받아올지, 여기서 설정할지, 아니면 다른 방식으로 설정할지 고민이 필요
            asyncService.sendAlarmAndSocketMessageAsync(Long.parseLong(roomNumber),chatRequest,sessionId);
//            List<String> allTokenBySessionId = redisService.getAllTokenBySessionId(sessionId);
            //test
            long afterTime = System.currentTimeMillis();
            log.info("time-spent-millis : {}",afterTime - beforeTime);
        }
        return message;
    }
}
