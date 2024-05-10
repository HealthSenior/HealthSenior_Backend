package out4ider.healthsenior.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import out4ider.healthsenior.service.RedisService;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompChatHandler {
    private final RedisService redisService;

    @EventListener
    public void stompDisconnected(SessionDisconnectEvent sessionDisconnectEvent){
        redisService.quitChatRoom(sessionDisconnectEvent.getSessionId());
        log.info("sessionId : {}", sessionDisconnectEvent.getSessionId());
        log.info("disconnected");
    }
}
