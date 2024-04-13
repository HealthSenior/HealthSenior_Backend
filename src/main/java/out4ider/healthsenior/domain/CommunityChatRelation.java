package out4ider.healthsenior.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CommunityChatRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long communityChatRelationId;
    @ManyToOne
    SeniorUser seniorUser;
    @ManyToOne
    CommunityChatRoom communityChatRoom;
}
