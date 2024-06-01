package out4ider.healthsenior.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class UtilConfig {
    @Bean
    public Map<String, WebSocketSession> webSocketSessionMap(){
        return new ConcurrentHashMap<>();
    }
}
