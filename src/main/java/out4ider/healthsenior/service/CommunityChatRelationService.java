package out4ider.healthsenior.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import out4ider.healthsenior.domain.CommunityChatRelation;
import out4ider.healthsenior.domain.CommunityChatRoom;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.repository.CommunityChatRelationRepository;
import out4ider.healthsenior.repository.UserFcmRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommunityChatRelationService {
    private final CommunityChatRelationRepository communityChatRelationRepository;
    private final RedisService redisService;

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

    public boolean isAlreadyJoined(Long chatRoomId, Long userId){
        int cnt = communityChatRelationRepository.countByChatRoomIdAndUserId(userId, chatRoomId);
        if (cnt > 0) return true;
        return false;
    }

    @Transactional
    public void exitChatRoom(Long chatRoomId, String oauth2Id){
        redisService.deleteTokenOfChatRoomUser(String.valueOf(chatRoomId),oauth2Id);
        communityChatRelationRepository.deleteBySeniorUserOauth2IdAndChatRoomId(oauth2Id,chatRoomId);
    }
}
