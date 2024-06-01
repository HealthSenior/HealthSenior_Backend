package out4ider.healthsenior.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class UnreadMessageDto {
    private Long chatRoomId;
    private String oauth2Id;
    private String userName;
    private String content;
    private LocalDateTime messageTime;
}
