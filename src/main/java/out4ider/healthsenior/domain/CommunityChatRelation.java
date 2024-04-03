package out4ider.healthsenior.domain;

import jakarta.persistence.*;

@Entity
public class CommunityChatRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long communityChatRelationId;
    @ManyToOne
    SeniorUser seniorUser;
    @ManyToOne
    CommunityChatRoom communityChatRoom;
}
