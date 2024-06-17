package out4ider.healthsenior.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import out4ider.healthsenior.domain.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findAll(Pageable pageable);

    @Query("select a from Article a join a.writer w where w.userId=?1")
    Page<Article> findAllByUserId(Long userId, Pageable pageable);

    @Query("select a from Article a where a.title like concat('%', :keyword, '%') or a.content like concat('%', :keyword, '%')")
    Page<Article> findAllByTitleOrContent(String keyword, Pageable pageable);

}
