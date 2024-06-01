package out4ider.healthsenior.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import out4ider.healthsenior.domain.Article;
import out4ider.healthsenior.domain.ImageFile;
import out4ider.healthsenior.domain.LikeUserRelation;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.dto.ArticleResponseDto;
import out4ider.healthsenior.service.ArticleService;
import out4ider.healthsenior.service.ImageFileService;
import out4ider.healthsenior.service.LikeUserRelationService;
import out4ider.healthsenior.service.SeniorUserService;

import java.io.IOException;
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
    private final ImageFileService imageFileService;

    @PostMapping("/create")
    @Transactional
    public void createArticle(@RequestParam String title, @RequestParam String content,@RequestParam List<MultipartFile> images , Principal principal) throws Exception {
        String name = principal.getName();
        SeniorUser seniorUser = seniorUserService.findByOauth2Id(name);

        Article article = Article.builder()
                .title(title)
                .content(content)
                .writer(seniorUser)
                .createdAt(LocalDateTime.now())
                .comments(new ArrayList<>())
                .imageFiles(new ArrayList<>())
                .likeUserRelations(new ArrayList<>())
                .build();
        articleService.saveArticle(article);
        seniorUser.createArticle(article);
        System.out.println(article.getId());
        for(MultipartFile image : images) {
            if(image.isEmpty()){
                continue;
            }
            else{
                imageFileService.uploadImageToFolder(article, image);
            }
        }
    }
    @GetMapping("/list")
    public List<ArticleResponseDto> getArticleList(@RequestParam(defaultValue = "0") int page) throws IOException {
        Page<Article> articles = articleService.getArticles(page);
        List<ArticleResponseDto> articleResponseDtos=new ArrayList<>();
        for(Article article : articles){
            List<byte[]> images = null;
            if(!article.getImageFiles().isEmpty())
                images = imageFileService.downloadImageFromFolder(article);
            articleResponseDtos.add(new ArticleResponseDto(article, images));
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
    @Transactional
    public int likeArticle(@PathVariable Long id, Principal principal) throws Exception {
        Optional<Article> oa = articleService.getArticleById(id);
        if (oa.isEmpty()) {
            throw new Exception();
        }
        Article article = oa.get();
        String name= principal.getName();
        SeniorUser seniorUser = seniorUserService.findByOauth2Id(name);
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
