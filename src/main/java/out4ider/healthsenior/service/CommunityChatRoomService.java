package out4ider.healthsenior.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import out4ider.healthsenior.domain.CommunityChatRoom;
import out4ider.healthsenior.repository.CommunityChatRoomRepository;

import java.util.List;

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
}
