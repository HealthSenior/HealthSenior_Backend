package out4ider.healthsenior.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import out4ider.healthsenior.domain.ChatMessage;
import out4ider.healthsenior.dto.ChatRequest;
import out4ider.healthsenior.dto.FcmSendDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncService {
    private final Map<String, WebSocketSession> webSocketSessionMap;
    private final RedisService redisService;
    private final FcmService fcmService;
    private final ChatMessageService chatMessageService;
    private final ObjectMapper objectMapper;
    private final ChatService chatService;

    @Async
    public void sendAlarmAndSocketMessageAsync(Long roomNumber,ChatRequest chatRequest, String sessionId){
        Map<Object, Object> allOauth2IdAndTokenBySessionId = redisService.getAllOauth2IdAndTokenBySessionId(sessionId);
        for (Map.Entry<Object,Object> entry : allOauth2IdAndTokenBySessionId.entrySet()){
            WebSocketSession sockSession = webSocketSessionMap.get((String)entry.getKey());
            try { //fcm으로 메시지 전송
                log.info("send FCM to {}", (String)entry.getValue());
                fcmService.sendMessageTo(FcmSendDto.builder()
                        .body(chatRequest.getContent())
                        .title(chatRequest.getUserName())
                        .build(), (String)entry.getValue());
            } catch (IOException e) {
                log.error("FCM send failed");
            }
            if (sockSession == null) {
                log.info("No session! saving in db...");
                chatMessageService.saveChat(ChatMessage.builder()
                        .chatRoomId(roomNumber)
                        .userName(chatRequest.getUserName())
                        .content(chatRequest.getContent())
                        .oauth2Id(chatRequest.getOauth2Id())
                        .messageTime(LocalDateTime.now()).build());
            }
            else{
                try {
                    sockSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatService.chatRequestToResponse(chatRequest))));
                } catch (IOException e) {
                    log.error("socket send failed");
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
