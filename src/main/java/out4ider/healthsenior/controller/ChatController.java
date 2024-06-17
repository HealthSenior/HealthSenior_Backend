package out4ider.healthsenior.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
import out4ider.healthsenior.dto.*;
import out4ider.healthsenior.service.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ChatController {
    private final CommunityChatRoomService communityChatRoomService;
    private final CommunityChatRelationService communityChatRelationService;
    private final SeniorUserService seniorUserService;
    private final ChatService chatService;
    private final RedisService redisService;
    private final ChatMessageService chatMessageService;
    private final FcmService fcmService;
    private final RabbitTemplate rabbitTemplate;

    @ResponseBody
    @GetMapping("/connection_test")
    public String test(){
        log.info("connection success");
        return "ok";
    }

    @MessageMapping("room.{chatRoomId}")
    public ChatResponse chat(@DestinationVariable Long chatRoomId, ChatRequest chatRequest){
        log.info("sending!");
        ChatResponse chatResponse = chatService.chatRequestToResponse(chatRequest);
        log.info("send chat : {}",chatResponse.getContent());
        rabbitTemplate.convertAndSend("chat.exchange","room."+chatRoomId,chatResponse);
        return chatResponse;
    }

    @ResponseBody
    @PostMapping("/chatroom/newchat")
    public void makeChat(@RequestBody NewChatDto newChatDto, Principal principal) throws Exception {
        CommunityChatRoom communityChatRoom = communityChatRoomService.saveChatRoom(newChatDto, principal.getName());
        log.info(principal.getName());
        SeniorUser seniorUser = seniorUserService.findByOauth2Id(principal.getName());
        communityChatRelationService.newChat(seniorUser,communityChatRoom);
        String fcmToken = fcmService.getFcmToken(principal.getName());
        redisService.putToken(String.valueOf(communityChatRoom.getChatRoomId()), principal.getName(), fcmToken);
    }

    @ResponseBody
    @PostMapping("/chatroom/joinchat/{chatRoom}")
    public void joinChat(@PathVariable Long chatRoom, Principal principal) throws Exception {
        log.info("join chat room : {}", chatRoom);
        SeniorUser seniorUser = seniorUserService.findByOauth2Id(principal.getName());
        CommunityChatRoom theChatRoom = communityChatRoomService.getChatRoom(chatRoom);

        if (communityChatRelationService.isAlreadyJoined(chatRoom, seniorUser.getUserId())) return;

        communityChatRelationService.newChat(seniorUser, theChatRoom);
        String fcmToken = fcmService.getFcmToken(principal.getName());
        redisService.putToken(String.valueOf(chatRoom), principal.getName(), fcmToken);
    }

    @ResponseBody
    @GetMapping("/chatroom/list")
    public List<ChatRoomResponseDto> chatRoomList(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "keyword", defaultValue = "") String keyword,Principal principal) throws Exception {
        SeniorUser seniorUser = seniorUserService.findByOauth2Id(principal.getName());
        List<CommunityChatRoom> chatRoomList = communityChatRoomService.getChatRoomList(page,keyword,seniorUser.getUserId());
        return chatRoomList.stream().map(CommunityChatRoom::toResponseDto).collect(Collectors.toList());

    }

    @ResponseBody
    @GetMapping("/chatroom/list/{category}")
    public List<ChatRoomResponseDto> chatRoomList(@PathVariable String category, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "keyword", defaultValue = "") String keyword,  Principal principal) throws Exception {
        SeniorUser seniorUser = seniorUserService.findByOauth2Id(principal.getName());
        List<CommunityChatRoom> chatRoomList = communityChatRoomService.getChatRoomListByCategory(category,keyword,page,seniorUser.getUserId());
        return chatRoomList.stream().map(CommunityChatRoom::toResponseDto).collect(Collectors.toList());

    }

    @ResponseBody
    @GetMapping("/chatroom/mylist")
    public List<ChatRoomResponseDto> myChatRoomList(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "keyword", defaultValue = "") String keyword, Principal principal) throws Exception {
        String name = principal.getName();
        SeniorUser seniorUser = seniorUserService.findByOauth2Id(name);
        List<CommunityChatRoom> myChatRoomList = communityChatRoomService.getMyChatRoomList(page,keyword, seniorUser.getUserId());
        return myChatRoomList.stream().map(CommunityChatRoom::toResponseDto).collect(Collectors.toList());
    }

    @GetMapping("/chatroom/mylist/{category}")
    public List<ChatRoomResponseDto> myChatRoomListWithCategory(@PathVariable String category, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "keyword", defaultValue = "") String keyword, Principal principal) throws Exception {
        String name = principal.getName();
        SeniorUser seniorUser = seniorUserService.findByOauth2Id(name);
        List<CommunityChatRoom> myChatRoomListByCategory = communityChatRoomService.getMyChatRoomListByCategory(category,keyword, page, seniorUser.getUserId());
        return myChatRoomListByCategory.stream().map(CommunityChatRoom::toResponseDto).collect(Collectors.toList());
    }

    @ResponseBody
    @GetMapping("/chat/get-unread")
    public List<UnreadMessageDto> getUnread(Principal principal){
        String oauth2Id = principal.getName();
        List<UnreadMessageDto> unSendChat = chatMessageService.getUnSendChat(oauth2Id);
        return unSendChat;
    }

    @ResponseBody
    @PostMapping("/chatroom/exitchat/{chatRoomId}")
    public void exitChat(@PathVariable Long chatRoomId, Principal principal){
        communityChatRelationService.exitChatRoom(chatRoomId,principal.getName());
    }
}
