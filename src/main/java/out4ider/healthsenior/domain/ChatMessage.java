package out4ider.healthsenior.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import out4ider.healthsenior.dto.UnreadMessageDto;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private Long chatRoomId;
    private String oauth2Id;
    private String userName;
    private String content;
    private String unreadUserOauth2Id;
    private LocalDateTime messageTime;

    public UnreadMessageDto toUnreadMessageDto(){
        return new UnreadMessageDto(chatRoomId,oauth2Id,userName,content,messageTime);
    }
}
