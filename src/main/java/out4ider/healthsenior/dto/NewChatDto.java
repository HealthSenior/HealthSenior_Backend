package out4ider.healthsenior.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewChatDto {
    private Integer maxUserCount;
    private String title;
    private String description;
    private String sportKind;
}
