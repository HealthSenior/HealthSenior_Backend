package out4ider.healthsenior.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class SeniorUser {
    @Id
    Long userId;
    @OneToMany(mappedBy = "senior_user")
    List<CommunityChatRelation> communityChatRelation;
    String userName;
    Integer userAge;
    boolean isMale;
}
