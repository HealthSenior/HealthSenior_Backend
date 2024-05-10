package out4ider.healthsenior.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import out4ider.healthsenior.domain.Article;
import out4ider.healthsenior.domain.CommunityChatRoom;
import out4ider.healthsenior.repository.ArticleRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    public Article saveArticle(Article article){
        Article saved = articleRepository.save(article);
        return saved;
    }

    public List<Article> getAllArticles(){
        List<Article> articles = articleRepository.findAll();
        return articles;
    }

    public Optional<Article> getArticleById(Long id){
        return articleRepository.findById(id);
    }

    public void deleteArticleById(Long id){
        articleRepository.deleteById(id);
    }

}
