package out4ider.healthsenior.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import out4ider.healthsenior.domain.CommunityChatRoom;

import java.util.List;

public interface CommunityChatRoomRepository extends JpaRepository<CommunityChatRoom,Long> {
    public List<CommunityChatRoom> findByChatRoomIdNotIn(List<Long> chatRoomIds);
//    Page<CommunityChatRoom> findByChatRoomIdNotIn(List<Long> chatRoomIds,Pageable pageable);

    @Query("select c from CommunityChatRoom c where not exists (select 1 from c.communityChatRelation m where m.seniorUser.userId = ?1)")
    Page<CommunityChatRoom> findByChatRoomIdNotIn(Long userId, Pageable pageable);

    @Query("select c from CommunityChatRoom c join c.communityChatRelation m where m.seniorUser.userId = ?1")
    Page<CommunityChatRoom> findAllByUserId(Long userId,Pageable pageable);

    @Query("select c from CommunityChatRoom c join c.communityChatRelation m where m.seniorUser.userId = ?1 and c.sportKind = ?2")
    Page<CommunityChatRoom> findAllByUserIdAndCategory(Long userId, String category, Pageable pageable);
}
