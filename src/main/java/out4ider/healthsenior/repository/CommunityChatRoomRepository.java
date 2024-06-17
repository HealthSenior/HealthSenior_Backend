package out4ider.healthsenior.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import out4ider.healthsenior.domain.CommunityChatRoom;

import java.util.List;

public interface CommunityChatRoomRepository extends JpaRepository<CommunityChatRoom,Long> {

    @Query("select c from CommunityChatRoom c where not exists (select 1 from c.communityChatRelation m where m.seniorUser.userId = ?1) and (c.title like concat('%',?2,'%') or c.description like concat('%',?2,'%'))")
    Page<CommunityChatRoom> findByChatRoomIdNotIn(Long userId, String keyword, Pageable pageable);

    @Query("select c from CommunityChatRoom c where not exists (select 1 from c.communityChatRelation m where m.seniorUser.userId = ?1) and c.sportKind = ?2 and (c.title like concat('%',?3,'%') or c.description like concat('%',?3,'%'))")
    Page<CommunityChatRoom> findByChatRoomIdNotInByCategory(Long userId, String category, String keyword, Pageable pageable);

    @Query("select c from CommunityChatRoom c join c.communityChatRelation m where m.seniorUser.userId = ?1 and (c.title like concat('%',?2,'%') or c.description like concat('%',?2,'%'))")
    Page<CommunityChatRoom> findAllByUserId(Long userId,String keyword,Pageable pageable);

    @Query("select c from CommunityChatRoom c join c.communityChatRelation m where m.seniorUser.userId = ?1 and c.sportKind = ?2 and (c.title like concat('%',?3,'%') or c.description like concat('%',?3,'%'))")
    Page<CommunityChatRoom> findAllByUserIdAndCategory(Long userId, String category, String keyword, Pageable pageable);
}
