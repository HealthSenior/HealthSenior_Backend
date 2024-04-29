package out4ider.healthsenior.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import out4ider.healthsenior.domain.CommunityChatRelation;
import out4ider.healthsenior.domain.CommunityChatRoom;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.repository.CommunityChatRelationRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityChatRelationService {
    private final CommunityChatRelationRepository communityChatRelationRepository;

    @Transactional
    public CommunityChatRelation newChat(SeniorUser seniorUser, CommunityChatRoom communityChatRoom){
        CommunityChatRelation chatRelation = CommunityChatRelation.builder()
                .seniorUser(seniorUser)
                .communityChatRoom(communityChatRoom)
                .build();
        seniorUser.joinChat(chatRelation);
        communityChatRoom.letInUser(chatRelation);
        CommunityChatRelation save = communityChatRelationRepository.save(chatRelation);
        return save;
    }
}
