package out4ider.healthsenior.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class NormalWebsocketHandler extends TextWebSocketHandler {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String,WebSocketSession> sessions;
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("connected");
        String oauth2Id = session.getUri().getQuery().split("=")[1];
        log.info("socket uri id : {}",oauth2Id);
        sessions.put(oauth2Id,session);
    }
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("Transport Error");
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("disconnected");
        String oauth2Id = session.getUri().getQuery().split("=")[1];
        sessions.remove(oauth2Id);
    }
}
