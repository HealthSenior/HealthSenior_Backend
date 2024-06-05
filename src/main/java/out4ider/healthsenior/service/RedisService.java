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
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.domain.UserFcmToken;
import out4ider.healthsenior.repository.CommunityChatRelationRepository;
import out4ider.healthsenior.repository.SeniorUserRepository;
import out4ider.healthsenior.repository.UserFcmRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;
    private final CommunityChatRelationRepository communityChatRelationRepository;
    private final UserFcmRepository userFcmRepository;
    private final SeniorUserRepository seniorUserRepository;


    public void putToken(String roomNumber,String oauth2Id,String token){
        HashOperations<String, Object, Object> stringObjectObjectHashOperations = redisTemplate.opsForHash();
        stringObjectObjectHashOperations.put(roomNumber,oauth2Id,token);
        log.info("putToken : {}", oauth2Id);
    }

    public void putRefreshToken(String username, String token, Long expiredTime){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(username, token, expiredTime, TimeUnit.MICROSECONDS);
    }

    public String getRefreshToken(String username){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(username);
    }

    public void deleteRefreshToken(String username){
        redisTemplate.delete(username);
    }

    public List<String> getAllTokenBySessionId(String sessionId){
        HashOperations<String, Object, Object> stringObjectObjectHashOperations = redisTemplate.opsForHash();
        String roomNumber = (String)stringObjectObjectHashOperations.get("roomNumber", sessionId);
        Map<Object, Object> entries = stringObjectObjectHashOperations.entries(roomNumber);
        List<String> values = entries.values().stream().map(p->(String)p).collect(Collectors.toList());
        log.info("allTokens : {}", values.toString());
        return values;
    }

    public Map<Object, Object> getAllOauth2IdAndTokenBySessionId(String sessionId){
        HashOperations<String, Object, Object> stringObjectObjectHashOperations = redisTemplate.opsForHash();
        String roomNumber = (String)stringObjectObjectHashOperations.get("roomNumber", sessionId);
        Map<Object, Object> entries = stringObjectObjectHashOperations.entries(roomNumber);
        return entries;
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
        log.info("roomNumber : {}, oauth2Id : {}, token : {}", roomNumber, oauth2Id, token);
        stringObjectObjectHashOperations.put(roomNumber,oauth2Id,token);
        log.info("quitChatRoom : {}", oauth2Id);
    }

    public void enterChatRoom(String sessionId,String roomNumber,String oauth2Id){
        HashOperations<String, Object, Object> stringObjectObjectHashOperations = redisTemplate.opsForHash();
        String token = (String)stringObjectObjectHashOperations.get(roomNumber, oauth2Id);
        log.info("enter token : {}", token);
        stringObjectObjectHashOperations.delete(roomNumber,oauth2Id);
        stringObjectObjectHashOperations.put("roomNumber",sessionId,roomNumber);
        stringObjectObjectHashOperations.put("oauth2Id",sessionId,oauth2Id);
        stringObjectObjectHashOperations.put("token",sessionId,token);
    }

    public void updateUserFcmToken(String oauth2Id, String fcmToken){
        Optional<SeniorUser> seniorUser = seniorUserRepository.findByOauth2Id(oauth2Id);
        if (seniorUser.isPresent()){
            SeniorUser user = seniorUser.get();
            HashOperations<String, Object, Object> hashOps = redisTemplate.opsForHash();
            List<CommunityChatRelation> communityChatRelations = user.getCommunityChatRelation();
            for (CommunityChatRelation chatRelation : communityChatRelations){
                hashOps.put(String.valueOf(chatRelation.getCommunityChatRoom().getChatRoomId()),oauth2Id,fcmToken);
            }
        }
        else{
            log.error("updateUserFcmToken method error");
        }
    }

    @PostConstruct
    public void init(){
        log.info("------------------initializing!!--------------");
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
        log.info("------------------initialized!!--------------");
    }
}
