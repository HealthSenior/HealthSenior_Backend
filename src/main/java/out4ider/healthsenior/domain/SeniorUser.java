package out4ider.healthsenior.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import out4ider.healthsenior.enums.Role;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeniorUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long userId;
    @OneToMany(mappedBy = "seniorUser", fetch = FetchType.LAZY)
    List<CommunityChatRelation> communityChatRelation = new ArrayList<>();
    String userName;
    String email;
    Integer userAge;
    boolean isMale;
    String oauth2Id;
    Role role;
}
