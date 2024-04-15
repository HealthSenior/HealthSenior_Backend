package out4ider.healthsenior.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import out4ider.healthsenior.enums.Role;

@Getter
@Setter
@AllArgsConstructor
public class UserAuthDto {
    private String oauth2Id;
    Role role;
}
