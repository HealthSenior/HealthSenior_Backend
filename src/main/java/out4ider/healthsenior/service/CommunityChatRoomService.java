package out4ider.healthsenior.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import out4ider.healthsenior.domain.CommunityChatRoom;
import out4ider.healthsenior.dto.NewChatDto;
import out4ider.healthsenior.repository.CommunityChatRoomRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityChatRoomService {
    private final CommunityChatRoomRepository communityChatRoomRepository;

    public CommunityChatRoom saveChatRoom(NewChatDto newChatDto, String oauth2Id){
        CommunityChatRoom communityChatRoom = CommunityChatRoom.builder()
                .title(newChatDto.getTitle())
                .description(newChatDto.getDescription())
                .masterId(oauth2Id)
                .maxUserCount(newChatDto.getMaxUserCount())
                .sportKind(newChatDto.getSportKind())
                .startDate(LocalDateTime.now())
                .communityChatRelation(new ArrayList<>())
                .build();
        return communityChatRoomRepository.save(communityChatRoom);
    }

    public List<CommunityChatRoom> getChatRoomList(int page, String keyword, Long userId){
        Pageable pageable = PageRequest.of(page,10, Sort.by("startDate").descending());

        return communityChatRoomRepository.findByChatRoomIdNotIn(userId,keyword,pageable).getContent();
    }

    public List<CommunityChatRoom> getChatRoomListByCategory(String category,String keyword,int page,Long userId){
        Pageable pageable = PageRequest.of(page,10, Sort.by("startDate").descending());

        return communityChatRoomRepository.findByChatRoomIdNotInByCategory(userId,category,keyword,pageable).getContent();
    }



    public CommunityChatRoom getChatRoom(Long chatRoomId) throws Exception {
        Optional<CommunityChatRoom> byId = communityChatRoomRepository.findById(chatRoomId);
        if (byId.isEmpty()){
            throw new Exception();
        }
        return byId.get();
    }

    public List<CommunityChatRoom> getMyChatRoomList(int page,String keyword, Long userId){
        Pageable pageable = PageRequest.of(page,10,Sort.by("startDate").descending());

        return communityChatRoomRepository.findAllByUserId(userId,keyword,pageable).getContent();
    }

    public List<CommunityChatRoom> getMyChatRoomListByCategory(String category, String keyword,int page,Long userId){
        Pageable pageable = PageRequest.of(page, 10, Sort.by("startDate").descending());
        return communityChatRoomRepository.findAllByUserIdAndCategory(userId,category,keyword,pageable).getContent();
    }
}
