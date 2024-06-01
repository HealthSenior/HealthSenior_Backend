package out4ider.healthsenior.domain;

import jakarta.persistence.*;
import lombok.*;
import out4ider.healthsenior.dto.ChatRoomResponseDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private LocalDateTime startDate;

    public void letInUser(CommunityChatRelation communityChatRelation){
        this.communityChatRelation.add(communityChatRelation);
    }

    public ChatRoomResponseDto toResponseDto(){
        return new ChatRoomResponseDto(this);
    }
}
