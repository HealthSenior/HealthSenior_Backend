package out4ider.healthsenior.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TokenLoginResponseDto {
    private boolean gender;
    private Integer age;
    private String email;
    private String name;
}
