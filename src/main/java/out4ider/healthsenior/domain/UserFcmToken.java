package out4ider.healthsenior.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFcmToken {
    @Id
    private String oauth2Id;
    private String fcmToken;

    public void updateFcmToken(String fcmToken){
        this.fcmToken = fcmToken;
    }
}
