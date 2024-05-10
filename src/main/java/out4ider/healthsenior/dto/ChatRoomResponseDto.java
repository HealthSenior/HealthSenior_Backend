package out4ider.healthsenior.dto;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.*;
import out4ider.healthsenior.domain.CommunityChatRelation;
import out4ider.healthsenior.domain.CommunityChatRoom;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChatRoomResponseDto {
    private Long chatRoomId;
    private Integer maxUserCount;
    private String title;
    private String description;
    private String masterId;
    private String sportKind;
    private LocalDate startDate;
    private Integer currentUserCount;
    private Double averageAge;

    public ChatRoomResponseDto(CommunityChatRoom communityChatRoom) {
        int sumOfAge = 0;
        this.chatRoomId = communityChatRoom.getChatRoomId();
        this.maxUserCount = communityChatRoom.getMaxUserCount();
        this.title = communityChatRoom.getTitle();
        this.description = communityChatRoom.getDescription();
        this.masterId = communityChatRoom.getMasterId();
        this.sportKind = communityChatRoom.getSportKind();
        this.startDate = communityChatRoom.getStartDate();
        List<CommunityChatRelation> communityChatRelations = communityChatRoom.getCommunityChatRelation();
        this.currentUserCount = communityChatRelations.size();
        for (CommunityChatRelation communityChatRelation : communityChatRelations){
            sumOfAge += communityChatRelation.getSeniorUser().getUserAge();
        }
        this.averageAge = (double)sumOfAge / currentUserCount;
    }
}
