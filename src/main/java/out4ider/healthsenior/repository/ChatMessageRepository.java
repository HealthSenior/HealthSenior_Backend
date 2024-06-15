package out4ider.healthsenior.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import out4ider.healthsenior.domain.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    public List<ChatMessage> findAllByUnreadUserOauth2Id(String oauth2Id);
    public void deleteAllByUnreadUserOauth2Id(String oauth2Id);
}
