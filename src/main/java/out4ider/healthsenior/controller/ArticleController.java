package out4ider.healthsenior.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import out4ider.healthsenior.domain.Article;
import out4ider.healthsenior.domain.LikeUserRelation;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.dto.ArticleResponseDto;
import out4ider.healthsenior.dto.NewArticleDto;
import out4ider.healthsenior.service.ArticleService;
import out4ider.healthsenior.service.LikeUserRelationService;
import out4ider.healthsenior.service.SeniorUserService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {
    private final SeniorUserService seniorUserService;
    private final ArticleService articleService;
    private final LikeUserRelationService likeUserRelationService;

    @PostMapping("/create")
    public void createArticle(@RequestBody NewArticleDto newArticleDto, Principal principal) throws Exception {
        String name = principal.getName();
        Optional<SeniorUser> op = seniorUserService.findByOauth2Id(name);
        if (op.isEmpty()) {
            throw new Exception();
        }
        SeniorUser seniorUser = op.get();
        Article article = Article.builder()
                .title(newArticleDto.getTitle())
                .content(newArticleDto.getContent())
                .writer(seniorUser)
                .createdAt(LocalDateTime.now())
                .comments(new ArrayList<>())
                .likeUserRelations(new ArrayList<>())
                .build();
        articleService.saveArticle(article);
        seniorUser.createArticle(article);
    }
    @GetMapping("/list")
    public List<ArticleResponseDto> getArticleList(){
        List<Article> articles = articleService.getAllArticles();
        List<ArticleResponseDto> articleResponseDtos=new ArrayList<>();
        for(Article article : articles){
            articleResponseDtos.add(new ArticleResponseDto(article));
        }
        return articleResponseDtos;
    }
    /*@PutMapping("/update{id}")
    public Article updateArticle(@PathVariable Long id, @RequestBody NewArticleDto newArticleDto) throws Exception {
        Optional<Article> op = articleService.getArticleById(id);
        if (op.isEmpty()) {
            throw new Exception();
        }
        Article article = op.get();
        article.setTitle(newArticleDto.getTitle());
        article.setContent(newArticleDto.getContent());
        article.setUpdatedAt(LocalDateTime.now());
        Article saved = articleService.saveArticle(article);
        return saved;
    }*/

    @DeleteMapping("/delete/{id}")
    public void deleteArticle(@PathVariable Long id, Principal principal) throws Exception {
        Optional<Article> op = articleService.getArticleById(id);
        if (op.isEmpty()) {
            throw new Exception();
        }
        Article article = op.get();
        if (!article.getWriter().getOauth2Id().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        articleService.deleteArticleById(id);
    }

    @PostMapping("/like/{id}")
    public int likeArticle(@PathVariable Long id, Principal principal) throws Exception {
        Optional<Article> oa = articleService.getArticleById(id);
        if (oa.isEmpty()) {
            throw new Exception();
        }
        Article article = oa.get();
        String name= principal.getName();
        Optional<SeniorUser> os = seniorUserService.findByOauth2Id(name);
        if (os.isEmpty()) {
            throw new Exception();
        }
        SeniorUser seniorUser = os.get();
        int count = article.getLikeUserRelations().size();
        List<LikeUserRelation> checked = article.getLikeUserRelations();
        for(LikeUserRelation likeUserRelation : checked){
            if(likeUserRelation.getSeniorUser().getUserId()==seniorUser.getUserId()){
                likeUserRelationService.deleteLikeUserRelation(likeUserRelation.getId());
                return count-1;
            }
        }
        LikeUserRelation likeUserRelation = LikeUserRelation.builder()
                .seniorUser(seniorUser)
                .article(article)
                .build();
        likeUserRelationService.saveLikeUserRelation(likeUserRelation);
        seniorUser.createLikeUserRelation(likeUserRelation);
        article.createLikeUserRelation(likeUserRelation);
        return count+1;
    }
}
