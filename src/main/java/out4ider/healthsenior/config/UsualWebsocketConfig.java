package out4ider.healthsenior.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import out4ider.healthsenior.handler.NormalWebsocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EnableWebSocket
@Configuration
@RequiredArgsConstructor
public class UsualWebsocketConfig implements WebSocketConfigurer {
    private final NormalWebsocketHandler webSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketHandler,"/ws/normal").setAllowedOrigins("*");
    }
}
