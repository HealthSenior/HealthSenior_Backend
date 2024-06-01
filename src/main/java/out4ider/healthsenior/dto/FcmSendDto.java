package out4ider.healthsenior.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
@Builder
public class FcmSendDto {
    private String title;
    private String body;
}
