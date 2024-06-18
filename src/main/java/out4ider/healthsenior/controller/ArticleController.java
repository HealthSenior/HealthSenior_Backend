package out4ider.healthsenior.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ArticleController {
    private final SeniorUserService seniorUserService;
    private final ArticleService articleService;
    private final LikeUserRelationService likeUserRelationService;

    @PostMapping("/create")
    public void createArticle(@RequestParam String title, @RequestParam String content,@RequestParam(required = false) List<MultipartFile> images , Principal principal) throws IOException {
        articleService.saveArticle(title, content, images, principal.getName());
    }
    @GetMapping("/list")
    public List<ArticleResponseDto> getArticleList(@RequestParam String keyword, @RequestParam(defaultValue = "0") int page) throws IOException {
        return articleService.getArticles(keyword, page);
    }

    @GetMapping("/mylist")
    public List<ArticleResponseDto> getMyArticleList(@RequestParam(defaultValue = "0") int page, Principal principal) throws IOException {
        SeniorUser seniorUser = seniorUserService.findByOauth2Id(principal.getName());
        return articleService.getMyArticles(seniorUser.getUserId(), page);
    }

    @GetMapping("/search")
    public List<ArticleResponseDto> searchArticleList(@RequestParam(defaultValue = "") String keyword, @RequestParam(defaultValue = "0") int page) throws IOException {
        return articleService.searchArticles(keyword,page);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteArticle(@PathVariable Long id, Principal principal) throws IOException {
        log.info("run controller");
        articleService.deleteArticleById(id, principal.getName());
    }

    @PostMapping("/like/{id}")
    public int likeArticle(@PathVariable Long id, Principal principal) {
        return likeUserRelationService.clickLike(id, principal.getName());
    }
}
