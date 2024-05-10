package out4ider.healthsenior.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import out4ider.healthsenior.jwt.JWTUtil;
import out4ider.healthsenior.service.RedisService;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class ChatPreHandle implements ChannelInterceptor {
    private final RedisService redisService;
    private final JWTUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
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
            log.info("send message! : {}", message.getPayload().toString());
            List<String> allTokenBySessionId = redisService.getAllTokenBySessionId(sessionId);
            for (String token : allTokenBySessionId){
                //fcm으로 메시지 전송
            }
        }
        return message;
    }
}
