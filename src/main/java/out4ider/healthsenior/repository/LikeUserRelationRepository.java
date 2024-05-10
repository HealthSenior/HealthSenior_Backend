package out4ider.healthsenior.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import out4ider.healthsenior.domain.LikeUserRelation;

public interface LikeUserRelationRepository extends JpaRepository<LikeUserRelation, Long> {
}
