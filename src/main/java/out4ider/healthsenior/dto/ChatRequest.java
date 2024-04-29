package out4ider.healthsenior.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatRequest {
    private Long userId;
    private String userName;
    private String content;
}
