package out4ider.healthsenior.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class CommunityChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    @OneToMany(mappedBy = "communityChatRoom", cascade = CascadeType.REMOVE)
    private List<CommunityChatRelation> communityChatRelation;

    @Column
    private Integer maxUserCount;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private String masterId;

    @Column
    private String sportKind;

    @Column
    private LocalDate startDate;

    public void letInUser(CommunityChatRelation communityChatRelation){
        this.communityChatRelation.add(communityChatRelation);
    }
}
