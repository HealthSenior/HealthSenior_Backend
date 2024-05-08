package out4ider.healthsenior.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import out4ider.healthsenior.domain.Article;

import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
