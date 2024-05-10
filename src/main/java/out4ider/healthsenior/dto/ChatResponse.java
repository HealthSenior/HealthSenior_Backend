package out4ider.healthsenior.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponse {
    private String oauth2Id;
    private String userName;
    private String content;
    private LocalDateTime messageTime;
}
