package out4ider.healthsenior.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommunityChatRelationRepository extends JpaRepository<out4ider.healthsenior.domain.CommunityChatRelation,Long> {
    @Query("select count(c) from CommunityChatRelation c join c.seniorUser m join c.communityChatRoom x where m.userId = ?1 and x.chatRoomId = ?2")
    public int countByChatRoomIdAndUserId(Long userId, Long chatRoomId);

    @Modifying
    @Query("delete from CommunityChatRelation c where c.seniorUser.oauth2Id = :oauth2Id and c.communityChatRoom.chatRoomId = :chatRoomId")
    public void deleteBySeniorUserOauth2IdAndChatRoomId(@Param("oauth2Id") String oauth2Id, @Param("chatRoomId") Long chatRoomId);
}
