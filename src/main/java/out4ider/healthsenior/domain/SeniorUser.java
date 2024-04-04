package out4ider.healthsenior.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class SeniorUser {
    @Id
    Long userId;
    @OneToMany(mappedBy = "seniorUser", fetch = FetchType.LAZY)
    List<CommunityChatRelation> communityChatRelation;
    String userName;
    Integer userAge;
    boolean isMale;
}
