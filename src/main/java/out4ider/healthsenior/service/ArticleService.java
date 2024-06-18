package out4ider.healthsenior.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import out4ider.healthsenior.domain.Article;
import out4ider.healthsenior.domain.ImageFile;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.dto.ArticleResponseDto;
import out4ider.healthsenior.exception.NotAuthorizedException;
import out4ider.healthsenior.exception.NotFoundElementException;
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
@Slf4j
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final SeniorUserService seniorUserService;
    private final ImageFileRepository imageFileRepository;
    private final String filePath = "/home/ubuntu/hsimage/";

    @Transactional
    public void saveArticle(String title, String content, List<MultipartFile> images, String name) throws IOException {
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
        if (images!=null && images.size()>0) {
            UUID uuid = null;
            for (MultipartFile image : images) {
                uuid = UUID.randomUUID();
                String fileName = uuid + image.getOriginalFilename();
                ImageFile imageFile = ImageFile.builder()
                        .fileName(fileName)
                        .article(article)
                        .build();
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

    public List<ArticleResponseDto> getArticles(String keyword, int page) throws IOException {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        log.info("repo first" + keyword);
        List<Article> articles = articleRepository.findAllByTitleOrContent(keyword, pageable).getContent();
        log.info("repo second");
        return getArticleResponseDtos(articles);
    }

    public List<ArticleResponseDto> getMyArticles(Long userId, int page) throws IOException {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createdAt").descending());
        List<Article> articles = articleRepository.findAllByUserId(userId, pageable).getContent();
        return getArticleResponseDtos(articles);
    }

    public List<ArticleResponseDto> getArticleResponseDtos(List<Article> articles) throws IOException {
        List<ArticleResponseDto> articleResponseDtos = new ArrayList<>();
        for (Article article : articles) {
            List<byte[]> images = new ArrayList<>();
            if (article.getImageFiles()!=null && article.getImageFiles().size()>0) {
                for (ImageFile imageFile : article.getImageFiles()) {
                    images.add(Files.readAllBytes(Path.of(filePath + imageFile.getFileName())));
                }
            }
            articleResponseDtos.add(new ArticleResponseDto(article, images));
        }
        return articleResponseDtos;
    }

    public Article getArticleById(Long id) {
        Optional<Article> oa = articleRepository.findById(id);
        if (oa.isEmpty()) {
            throw new NotFoundElementException(1, "That is not in DB", HttpStatus.NOT_FOUND);
        }
        return oa.get();
    }

    public void deleteArticleById(Long id, String name) throws IOException {
        Article article = getArticleById(id);
        if (!article.getWriter().getOauth2Id().equals(name)) {
            throw new NotAuthorizedException(3, "Don't have permission to delete this article", HttpStatus.FORBIDDEN);
        }
        List<ImageFile> images = article.getImageFiles();
        if (images !=null && images.size()>0) {
            for (ImageFile imageFile : images) {
                Files.deleteIfExists(Path.of(filePath + imageFile.getFileName()));
            }
        }
        articleRepository.deleteById(id);
    }
}
