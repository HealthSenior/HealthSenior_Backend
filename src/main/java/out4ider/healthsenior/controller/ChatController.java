package out4ider.healthsenior.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import out4ider.healthsenior.dto.ChatRequest;

@RequiredArgsConstructor
@Controller
public class ChatController {

    @MessageMapping("/chatroom/{chatRoomId}")
    @SendTo("/subscribe_room/{chatRoomId}")
    public ChatRequest chat(@DestinationVariable Long chatRoomId, ChatRequest chatRequest){
        return chatRequest;
    }

    @ResponseBody
    @GetMapping("/connection_test")
    public String test(){
        return "ok";
    }
}
