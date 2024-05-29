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


//            List<String> allTokenBySessionId = redisService.getAllTokenBySessionId(sessionId);
            Map<Object, Object> allOauth2IdAndTokenBySessionId = redisService.getAllOauth2IdAndTokenBySessionId(sessionId);
            for (Map.Entry<Object,Object> entry : allOauth2IdAndTokenBySessionId.entrySet()){
                WebSocketSession sockSession = webSocketSessionMap.get((String)entry.getKey());
                if (sockSession == null) { //fcm으로 메시지 전송
                    log.info("No session! saving in db...");
                    chatMessageService.saveChat(ChatMessage.builder()
                            .userName(chatRequest.getUserName())
                            .content(chatRequest.getContent())
                            .oauth2Id(chatRequest.getOauth2Id())
                            .messageTime(LocalDateTime.now()).build());
                }
                else{
                    try {
                        sockSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatRequest)));
                    } catch (IOException e) {
                        log.error("socket send failed");
                        throw new RuntimeException(e);
                    }
                    try {
                        log.info("send FCM to {}", (String)entry.getValue());
                        fcmService.sendMessageTo(FcmSendDto.builder()
                                .body(chatRequest.getContent())
                                .title(chatRequest.getUserName())
                                .build(), (String)entry.getValue());
                    } catch (IOException e) {
                        log.error("FCM send failed");
                    }
                }
            }
        }
        return message;
    }
}
