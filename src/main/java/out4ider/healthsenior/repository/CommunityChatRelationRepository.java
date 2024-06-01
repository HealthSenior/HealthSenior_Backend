package out4ider.healthsenior.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommunityChatRelationRepository extends JpaRepository<out4ider.healthsenior.domain.CommunityChatRelation,Long> {
    @Query("select count(c) from CommunityChatRelation c join c.seniorUser m join c.communityChatRoom x where m.userId = ?1 and x.chatRoomId = ?2")
    public int countByChatRoomIdAndUserId(Long userId, Long chatRoomId);
}
