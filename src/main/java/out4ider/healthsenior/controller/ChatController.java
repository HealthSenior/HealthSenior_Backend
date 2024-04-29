package out4ider.healthsenior.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import out4ider.healthsenior.domain.CommunityChatRelation;
import out4ider.healthsenior.domain.CommunityChatRoom;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.dto.ChatRequest;
import out4ider.healthsenior.dto.ChatResponse;
import out4ider.healthsenior.dto.NewChatDto;
import out4ider.healthsenior.service.ChatService;
import out4ider.healthsenior.service.CommunityChatRelationService;
import out4ider.healthsenior.service.CommunityChatRoomService;
import out4ider.healthsenior.service.SeniorUserService;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatController {
    private final CommunityChatRoomService communityChatRoomService;
    private final CommunityChatRelationService communityChatRelationService;
    private final SeniorUserService seniorUserService;
    private final ChatService chatService;

    @ResponseBody
    @GetMapping("/connection_test")
    public String test(){
        System.out.println("!");
        return "ok";
    }

    @MessageMapping("/chatroom/{chatRoomId}")
    @SendTo("/subscribe_room/{chatRoomId}")
    public ChatResponse chat(@DestinationVariable Long chatRoomId, ChatRequest chatRequest){
        ChatResponse chatResponse = chatService.chatRequestToResponse(chatRequest);
        return chatResponse;
    }

    @ResponseBody
    @PostMapping("/chatroom/newchat")
    public CommunityChatRoom makeChat(@RequestBody NewChatDto newChatDto, Principal principal) throws Exception {
        CommunityChatRoom communityChatRoom = CommunityChatRoom.builder()
                .title(newChatDto.getTitle())
                .description(newChatDto.getDescription())
                .masterId(principal.getName())
                .maxUserCount(newChatDto.getMaxUserCount())
                .sportKind(newChatDto.getSportKind())
                .startDate(LocalDate.now())
                .build();
        communityChatRoomService.saveChatRoom(communityChatRoom);
        log.info(principal.getName());
        Optional<SeniorUser> byOauth2Id = seniorUserService.findByOauth2Id(principal.getName());
        if (byOauth2Id.isEmpty()){
            throw new Exception();
        }
        SeniorUser seniorUser = byOauth2Id.get();
        communityChatRelationService.newChat(seniorUser,communityChatRoom);
        return communityChatRoom;
    }

    @ResponseBody
    @PostMapping("/chatroom/joinchat/{chatRoom}")
    public CommunityChatRelation joinChat(@PathVariable Long chatRoom, Principal principal) throws Exception {
        Optional<SeniorUser> byOauth2Id = seniorUserService.findByOauth2Id(principal.getName());
        if (byOauth2Id.isEmpty()){
            throw new Exception();
        }
        CommunityChatRoom theChatRoom = communityChatRoomService.getChatRoom(chatRoom);
        SeniorUser seniorUser = byOauth2Id.get();
        CommunityChatRelation chatRelation = communityChatRelationService.newChat(seniorUser, theChatRoom);
        return chatRelation;
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
