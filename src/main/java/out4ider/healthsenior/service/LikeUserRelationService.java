package out4ider.healthsenior.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import out4ider.healthsenior.domain.Article;
import out4ider.healthsenior.domain.LikeUserRelation;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.repository.LikeUserRelationRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeUserRelationService {
    private final LikeUserRelationRepository likeUserRelationRepository;
    private final ArticleService articleService;
    private final SeniorUserService seniorUserService;

    @Transactional
    public int clickLike(Long id, String name) {
        SeniorUser seniorUser = seniorUserService.findByOauth2Id(name);
        int count = likeUserRelationRepository.countByArticleId(id);
        Optional<LikeUserRelation> ol = likeUserRelationRepository.isClickArticle(id, seniorUser.getUserId());
        if(ol.isPresent()) {
            LikeUserRelation likeUserRelation = ol.get();
            likeUserRelationRepository.deleteById(likeUserRelation.getId());
            return count-1;
        }
        Article article = articleService.getArticleById(id);
        LikeUserRelation likeUserRelation = LikeUserRelation.builder()
                .seniorUser(seniorUser)
                .article(article)
                .build();
        likeUserRelationRepository.save(likeUserRelation);
        seniorUser.createLikeUserRelation(likeUserRelation);
        article.createLikeUserRelation(likeUserRelation);
        return count+1;
    }
}
