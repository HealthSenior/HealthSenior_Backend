package out4ider.healthsenior.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import out4ider.healthsenior.dto.ChatRequest;
import out4ider.healthsenior.dto.ChatResponse;

@RequiredArgsConstructor
@Service
public class ChatService {
    public ChatResponse chatRequestToResponse(ChatRequest chatRequest){
        ChatResponse response = ChatResponse.builder()
                .userId(chatRequest.getUserId())
                .userName(chatRequest.getUserName())
                .content(chatRequest.getContent())
                .build();
        return response;
    }
}
