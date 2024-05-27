package out4ider.healthsenior.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import out4ider.healthsenior.domain.ChatMessage;
import out4ider.healthsenior.dto.ChatResponse;
import out4ider.healthsenior.repository.ChatMessageRepository;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    public void saveChat(ChatMessage chatMessage){
        chatMessageRepository.save(chatMessage);
    }
}
