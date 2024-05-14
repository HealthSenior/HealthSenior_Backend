package out4ider.healthsenior.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import out4ider.healthsenior.domain.CommunityChatRelation;
import out4ider.healthsenior.domain.UserFcmToken;
import out4ider.healthsenior.repository.CommunityChatRelationRepository;
import out4ider.healthsenior.repository.UserFcmRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private final CommunityChatRelationRepository communityChatRelationRepository;
    private final UserFcmRepository userFcmRepository;

    public void putToken(String roomNumber,String oauth2Id,String token){
        HashOperations<String, Object, Object> stringObjectObjectHashOperations = redisTemplate.opsForHash();
        stringObjectObjectHashOperations.put(roomNumber,oauth2Id,token);
        log.info("putToken : {}", oauth2Id);
    }

    public List<String> getAllTokenBySessionId(String sessionId){
        HashOperations<String, Object, Object> stringObjectObjectHashOperations = redisTemplate.opsForHash();
        String roomNumber = (String)stringObjectObjectHashOperations.get("roomNumber", sessionId);
        Map<Object, Object> entries = stringObjectObjectHashOperations.entries(roomNumber);
        List<String> values = entries.values().stream().map(p->(String)p).collect(Collectors.toList());
        log.info("allTokens : {}", values.toString());
        return values;
    }

    public void deleteTokenOfChatRoomUser(String roomNumber, String oauth2Id){
        HashOperations<String, Object, Object> stringObjectObjectHashOperations = redisTemplate.opsForHash();
        stringObjectObjectHashOperations.delete(roomNumber,oauth2Id);
        log.info("deleteToken : {}", oauth2Id);
    }

    public void quitChatRoom(String sessionId){
        HashOperations<String, Object, Object> stringObjectObjectHashOperations = redisTemplate.opsForHash();
        String roomNumber = (String)stringObjectObjectHashOperations.get("roomNumber", sessionId);
        String oauth2Id = (String)stringObjectObjectHashOperations.get("oauth2Id", sessionId);
        String token = (String)stringObjectObjectHashOperations.get("token", sessionId);
        System.out.println("roomNumber : " + roomNumber + "oauth2Id:" + oauth2Id + "token : " + token);
        stringObjectObjectHashOperations.put(roomNumber,oauth2Id,token);
        log.info("quitChatRoom : {}", oauth2Id);
    }

    public void enterChatRoom(String sessionId,String roomNumber,String oauth2Id){
        HashOperations<String, Object, Object> stringObjectObjectHashOperations = redisTemplate.opsForHash();
        String token = (String)stringObjectObjectHashOperations.get(roomNumber, oauth2Id);
        System.out.println("enter token : " + token);
        stringObjectObjectHashOperations.delete(roomNumber,oauth2Id);
        stringObjectObjectHashOperations.put("roomNumber",sessionId,roomNumber);
        stringObjectObjectHashOperations.put("oauth2Id",sessionId,oauth2Id);
        stringObjectObjectHashOperations.put("token",sessionId,token);
    }

    @PostConstruct
    public void init(){
        System.out.println("------------------initializing!!--------------");
        List<CommunityChatRelation> all = communityChatRelationRepository.findAll();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
        HashOperations<String, Object, Object> stringObjectObjectHashOperations = redisTemplate.opsForHash();
        for (CommunityChatRelation chatRelation : all){
            String oauth2Id = chatRelation.getSeniorUser().getOauth2Id();
            Long chatRoomId = chatRelation.getCommunityChatRoom().getChatRoomId();
            Optional<UserFcmToken> byId = userFcmRepository.findById(oauth2Id);
            if (byId.isPresent()){
                UserFcmToken userFcmToken = byId.get();
                String fcmToken = userFcmToken.getFcmToken();
                stringObjectObjectHashOperations.put(String.valueOf(chatRoomId),oauth2Id,fcmToken);
            }
        }
        System.out.println("------------------initialized!!--------------");
    }
}
