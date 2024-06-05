package out4ider.healthsenior.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import out4ider.healthsenior.domain.LikeUserRelation;

import java.util.Optional;

public interface LikeUserRelationRepository extends JpaRepository<LikeUserRelation, Long> {
    @Query("select l from LikeUserRelation l where l.article.id=?1 and l.seniorUser.userId=?2")
    Optional<LikeUserRelation> isClickArticle(Long articleId, Long seniorUserId);

    int countByArticleId(Long articleId);
}
