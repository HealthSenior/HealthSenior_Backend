package out4ider.healthsenior.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import out4ider.healthsenior.domain.CommunityChatRoom;
import out4ider.healthsenior.repository.CommunityChatRoomRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommunityChatRoomService {
    private final CommunityChatRoomRepository communityChatRoomRepository;

    public CommunityChatRoom saveChatRoom(CommunityChatRoom communityChatRoom){
        CommunityChatRoom saved = communityChatRoomRepository.save(communityChatRoom);
        return saved;
    }

    public List<CommunityChatRoom> getChatRoomList(){
        List<CommunityChatRoom> allChatRoom = communityChatRoomRepository.findAll();
        return allChatRoom;
    }

    public CommunityChatRoom getChatRoom(Long chatRoomId) throws Exception {
        Optional<CommunityChatRoom> byId = communityChatRoomRepository.findById(chatRoomId);
        if (byId.isEmpty()){
            throw new Exception();
        }
        return byId.get();
    }
}
