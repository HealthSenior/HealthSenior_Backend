package out4ider.healthsenior.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import out4ider.healthsenior.dto.ChatRequest;
import out4ider.healthsenior.dto.ChatResponse;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class ChatService {
    public ChatResponse chatRequestToResponse(ChatRequest chatRequest){
        ChatResponse response = ChatResponse.builder()
                .oauth2Id(chatRequest.getOauth2Id())
                .userName(chatRequest.getUserName())
                .content(chatRequest.getContent())
                .messageTime(LocalDateTime.now())
                .build();
        return response;
    }
}
