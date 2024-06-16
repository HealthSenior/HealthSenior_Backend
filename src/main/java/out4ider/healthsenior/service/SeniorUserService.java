package out4ider.healthsenior.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.dto.UserDto;
import out4ider.healthsenior.enums.Role;
import out4ider.healthsenior.exception.NotFoundElementException;
import out4ider.healthsenior.repository.SeniorUserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SeniorUserService {
    private final SeniorUserRepository seniorUserRepository;

    public SeniorUser findByOauth2Id(String oauth2Id) {
        Optional<SeniorUser> byOauthId = seniorUserRepository.findByOauth2Id(oauth2Id);
        if (byOauthId.isEmpty()){
            throw new NotFoundElementException(1, "That is not in DB", HttpStatus.NOT_FOUND);
        }
        return byOauthId.get();
    }

    public SeniorUser saveSeniorUser(UserDto userDto){
        String registrationId = userDto.getRegistrationId();
        String oauth2Id = registrationId + userDto.getUserId();
        boolean isMale = false;
        if (userDto.getGender() == null || userDto.getGender().equals("m")) {
            isMale = true;
        }
        Optional<SeniorUser> byOauth2Id = seniorUserRepository.findByOauth2Id(oauth2Id);
        SeniorUser seniorUser;
        if (byOauth2Id.isEmpty()) {
            seniorUser = SeniorUser.builder()
                    .userAge(LocalDateTime.now().getYear() - Integer.parseInt(userDto.getBirthYear()) + 1)
                    .userName(userDto.getName())
                    .email(userDto.getEmail()).role(Role.USER).oauth2Id(oauth2Id)
                    .communityChatRelation(new ArrayList<>())
                    .articleList(new ArrayList<>())
                    .commentList(new ArrayList<>())
                    .likeUserRelations(new ArrayList<>())
                    .isMale(isMale).build();
            seniorUserRepository.save(seniorUser);
        } else {
            seniorUser = byOauth2Id.get();
        }
        return seniorUser;
    }


}
