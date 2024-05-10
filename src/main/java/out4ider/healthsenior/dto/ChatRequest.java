package out4ider.healthsenior.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class  ChatRequest {
    private String oauth2Id;
    private String userName;
    private String content;
}
