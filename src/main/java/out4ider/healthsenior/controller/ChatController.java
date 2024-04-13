package out4ider.healthsenior.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import out4ider.healthsenior.domain.CommunityChatRoom;
import out4ider.healthsenior.dto.ChatRequest;
import out4ider.healthsenior.dto.NewChatDto;
import out4ider.healthsenior.service.CommunityChatRoomService;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class ChatController {
    private final CommunityChatRoomService communityChatRoomService;

    @ResponseBody
    @GetMapping("/connection_test")
    public String test(){
        System.out.println("!");
        return "ok";
    }

    @MessageMapping("/chatroom/{chatRoomId}")
    @SendTo("/subscribe_room/{chatRoomId}")
    public ChatRequest chat(@DestinationVariable Long chatRoomId, ChatRequest chatRequest){
        return chatRequest;
    }

    @ResponseBody
    @PostMapping("/chatroom/newchat")
    public CommunityChatRoom makeChat(@RequestBody NewChatDto newChatDto){
        CommunityChatRoom communityChatRoom = CommunityChatRoom.builder()
                .title(newChatDto.getTitle())
                .description(newChatDto.getDescription())
                .build();
        communityChatRoomService.saveChatRoom(communityChatRoom);
        return communityChatRoom;
    }

    @ResponseBody
    @GetMapping("/chatroom/list")
    public List<CommunityChatRoom> chatRoomList(){
        List<CommunityChatRoom> chatRoomList = communityChatRoomService.getChatRoomList();
        for (CommunityChatRoom x : chatRoomList){
            System.out.println(x.toString());
        }
        return chatRoomList;
    }
}
