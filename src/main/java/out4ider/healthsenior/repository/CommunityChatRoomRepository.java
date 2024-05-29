package out4ider.healthsenior.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import out4ider.healthsenior.domain.CommunityChatRoom;

import java.util.List;

public interface CommunityChatRoomRepository extends JpaRepository<CommunityChatRoom,Long> {
    public List<CommunityChatRoom> findByChatRoomIdNotIn(List<Long> chatRoomIds);
}
