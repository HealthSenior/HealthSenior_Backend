package out4ider.healthsenior.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import out4ider.healthsenior.domain.ChatMessage;
import out4ider.healthsenior.dto.ChatResponse;
import out4ider.healthsenior.repository.ChatMessageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    public void saveChat(ChatMessage chatMessage){
        chatMessageRepository.save(chatMessage);
    }
    @Transactional
    public List<ChatMessage> getUnSendChat(String oauth2Id){
        List<ChatMessage> allByOauth2Id = chatMessageRepository.findAllByOauth2Id(oauth2Id);
        chatMessageRepository.deleteAllByOauth2Id(oauth2Id);
        return allByOauth2Id;
    }
}
