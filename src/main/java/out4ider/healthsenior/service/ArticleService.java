package out4ider.healthsenior.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import out4ider.healthsenior.domain.Article;
import out4ider.healthsenior.domain.CommunityChatRoom;
import out4ider.healthsenior.domain.ImageFile;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.dto.ArticleResponseDto;
import out4ider.healthsenior.repository.ArticleRepository;
import out4ider.healthsenior.repository.ImageFileRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final SeniorUserService seniorUserService;
    private final ImageFileRepository imageFileRepository;

    @Transactional
    public void saveArticle(String title, String content, List<MultipartFile> images, String name) throws Exception {
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
        articleRepository.save(article);
        seniorUser.createArticle(article);
        System.out.println(article.getId());
        for (MultipartFile image : images) {
            String filePath = "images/";
            if (image.isEmpty()) {
                continue;
            } else {
                UUID uuid = UUID.randomUUID();
                String fileName = uuid + image.getOriginalFilename();
                ImageFile imageFile = ImageFile.builder()
                        .fileName(fileName)
                        .article(article)
                        .build();
                Files.createDirectories(Path.of(filePath));
                article.addImage(imageFile);
                imageFileRepository.save(imageFile);
                image.transferTo(new File(filePath + fileName));
            }
        }
    }

    public List<ArticleResponseDto> getArticles(int page) throws IOException {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        List<Article> articles = articleRepository.findAll(pageable).getContent();
        return getArticleResponseDtos(articles);
    }

    public List<ArticleResponseDto> getMyArticles(Long userId, int page) throws IOException {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        List<Article> articles = articleRepository.findAllByUserId(userId,pageable).getContent();
        return getArticleResponseDtos(articles);
    }

    public List<ArticleResponseDto> getArticleResponseDtos(List<Article> articles) throws IOException {
        List<ArticleResponseDto> articleResponseDtos = new ArrayList<>();
        String filePath = "images/";
        for (Article article : articles) {
            List<byte[]> images = new ArrayList<>();
            if (!article.getImageFiles().isEmpty()) {
                for (ImageFile imageFile : article.getImageFiles()) {
                    images.add(Files.readAllBytes(Path.of(filePath + imageFile.getFileName())));
                }
            }
            articleResponseDtos.add(new ArticleResponseDto(article, images));
        }
        return articleResponseDtos;
    }

    public Article getArticleById(Long id) throws Exception {
        Optional<Article> oa = articleRepository.findById(id);
        if(oa.isEmpty()){
            throw new Exception();
        }
        return oa.get();
    }

    public void deleteArticleById(Long id, String name) throws Exception {
        Article article = getArticleById(id);
        if (!article.getWriter().getOauth2Id().equals(name)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
        }
        articleRepository.deleteById(id);
    }
}
