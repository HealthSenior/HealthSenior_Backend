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
import out4ider.healthsenior.domain.ChatMessage;
import out4ider.healthsenior.domain.CommunityChatRelation;
import out4ider.healthsenior.domain.CommunityChatRoom;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.dto.ChatRequest;
import out4ider.healthsenior.dto.ChatResponse;
import out4ider.healthsenior.dto.ChatRoomResponseDto;
import out4ider.healthsenior.dto.NewChatDto;
import out4ider.healthsenior.service.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@Slf4j
public class ChatController {
    private final CommunityChatRoomService communityChatRoomService;
    private final CommunityChatRelationService communityChatRelationService;
    private final SeniorUserService seniorUserService;
    private final ChatService chatService;
    private final RedisService redisService;
    private final ChatMessageService chatMessageService;

    @ResponseBody
    @GetMapping("/connection_test")
    public String test(){
        log.info("connection success");
        return "ok";
    }

    @MessageMapping("/chatroom/{chatRoomId}")
    @SendTo("/subscribe_room/{chatRoomId}")
    public ChatResponse chat(@DestinationVariable Long chatRoomId, ChatRequest chatRequest){
        ChatResponse chatResponse = chatService.chatRequestToResponse(chatRequest);
        log.info("send chat : {}",chatResponse.getContent());
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
                .startDate(LocalDateTime.now())
                .communityChatRelation(new ArrayList<>())
                .build();
        CommunityChatRoom newChatRoom = communityChatRoomService.saveChatRoom(communityChatRoom);
        log.info(principal.getName());
        Optional<SeniorUser> byOauth2Id = seniorUserService.findByOauth2Id(principal.getName());
        if (byOauth2Id.isEmpty()){
            throw new Exception();
        }
        SeniorUser seniorUser = byOauth2Id.get();
        communityChatRelationService.newChat(seniorUser,communityChatRoom);
        String fcmToken = seniorUserService.getFcmToken(principal.getName());
        redisService.putToken(String.valueOf(newChatRoom.getChatRoomId()), principal.getName(), fcmToken);
        return communityChatRoom;
    }

    @ResponseBody
    @PostMapping("/chatroom/joinchat/{chatRoom}")
    public void joinChat(@PathVariable Long chatRoom, Principal principal) throws Exception {
        log.info("join chat room : {}", chatRoom);
        Optional<SeniorUser> byOauth2Id = seniorUserService.findByOauth2Id(principal.getName());
        if (byOauth2Id.isEmpty()){
            throw new Exception();
        }
        CommunityChatRoom theChatRoom = communityChatRoomService.getChatRoom(chatRoom);
        SeniorUser seniorUser = byOauth2Id.get();
        List<CommunityChatRelation> communityChatRelation = theChatRoom.getCommunityChatRelation();
        //이후 최적화 필요 할듯
        for (CommunityChatRelation chatRel : communityChatRelation){
            if (chatRel.getSeniorUser().getOauth2Id().equals(seniorUser.getOauth2Id())){
                return ;
            }
        }
        CommunityChatRelation chatRelation = communityChatRelationService.newChat(seniorUser, theChatRoom);
        String fcmToken = seniorUserService.getFcmToken(principal.getName());
        redisService.putToken(String.valueOf(chatRoom), principal.getName(), fcmToken);
    }

    @ResponseBody
    @GetMapping("/chatroom/list")
    public List<ChatRoomResponseDto> chatRoomList(@RequestParam(value = "page", defaultValue = "0") int page,  Principal principal) throws Exception {
        List<Long> chatRoomIds = new ArrayList<>();
        Optional<SeniorUser> os = seniorUserService.findByOauth2Id(principal.getName());
        if(os.isEmpty()){
            throw new Exception();
        }
        SeniorUser seniorUser = os.get();
        List<CommunityChatRoom> chatRoomList = communityChatRoomService.getChatRoomList(page,seniorUser.getUserId());
        for (CommunityChatRoom x : chatRoomList){
            log.info(x.toString());
        }
        List<ChatRoomResponseDto> chatRoomResponseList = new ArrayList<>();
        for (CommunityChatRoom communityChatRoom : chatRoomList){
            chatRoomResponseList.add(communityChatRoom.toResponseDto());
        }
        return chatRoomResponseList;
    }

    @ResponseBody
    @GetMapping("/chatroom/mylist")
    public List<ChatRoomResponseDto> myChatRoomList(@RequestParam(value = "page", defaultValue = "0") int page, Principal principal) throws Exception {
        String name = principal.getName();
        Optional<SeniorUser> byOauth2Id = seniorUserService.findByOauth2Id(name);
        if (byOauth2Id.isEmpty()) throw new Exception();

        List<CommunityChatRoom> myChatRoomList = communityChatRoomService.getMyChatRoomList(page, byOauth2Id.get().getUserId());
        List<ChatRoomResponseDto> collect = myChatRoomList.stream().map((a) -> a.toResponseDto()).collect(Collectors.toList());
        return collect;
    }

    @ResponseBody
    @GetMapping("/chat/get-unread")
    public List<ChatMessage> getUnread(Principal principal){
        String oauth2Id = principal.getName();
        List<ChatMessage> unSendChat = chatMessageService.getUnSendChat(oauth2Id);
        return unSendChat;
    }
}
