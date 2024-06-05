package out4ider.healthsenior.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.dto.ArticleResponseDto;
import out4ider.healthsenior.service.ArticleService;
import out4ider.healthsenior.service.LikeUserRelationService;
import out4ider.healthsenior.service.SeniorUserService;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {
    private final SeniorUserService seniorUserService;
    private final ArticleService articleService;
    private final LikeUserRelationService likeUserRelationService;

    @PostMapping("/create")
    public void createArticle(@RequestParam String title, @RequestParam String content,@RequestParam List<MultipartFile> images , Principal principal) throws Exception {
        articleService.saveArticle(title, content, images, principal.getName());
    }
    @GetMapping("/list")
    public List<ArticleResponseDto> getArticleList(@RequestParam(defaultValue = "0") int page) throws IOException {
        return articleService.getArticles(page);
    }

    @GetMapping("/mylist")
    public List<ArticleResponseDto> getMyArticleList(@RequestParam(defaultValue = "0") int page, Principal principal) throws Exception {
        SeniorUser seniorUser = seniorUserService.findByOauth2Id(principal.getName());
        return articleService.getMyArticles(seniorUser.getUserId(), page);
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
        articleService.deleteArticleById(id, principal.getName());
    }

    @PostMapping("/like/{id}")
    public int likeArticle(@PathVariable Long id, Principal principal) throws Exception {
        return likeUserRelationService.clickLike(id, principal.getName());
    }
}
