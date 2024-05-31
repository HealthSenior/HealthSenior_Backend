package out4ider.healthsenior.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import out4ider.healthsenior.domain.Article;
import out4ider.healthsenior.domain.CommunityChatRoom;
import out4ider.healthsenior.repository.ArticleRepository;

import java.util.ArrayList;
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

    public Page<Article> getArticles(int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page,10, Sort.by(sorts));
        Page<Article> articles = articleRepository.findAll(pageable);
        return articles;
    }

    public Optional<Article> getArticleById(Long id){
        return articleRepository.findById(id);
    }

    public void deleteArticleById(Long id){
        articleRepository.deleteById(id);
    }

}
