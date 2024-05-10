package out4ider.healthsenior.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import out4ider.healthsenior.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
