package out4ider.healthsenior.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import out4ider.healthsenior.domain.Article;
import out4ider.healthsenior.domain.Comment;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.dto.NewCommentDto;
import out4ider.healthsenior.repository.ArticleRepository;
import out4ider.healthsenior.repository.CommentRepository;
import out4ider.healthsenior.repository.SeniorUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final SeniorUserService seniorUserService;
    private final ArticleService articleService;

    public int saveComment(Long articleId, NewCommentDto newCommentDto, String name) {
        Article article = articleService.getArticleById(articleId);
        int count = commentRepository.countByArticleId(articleId);
        SeniorUser seniorUser = seniorUserService.findByOauth2Id(name);
        Comment comment = Comment.builder()
                .createdAt(LocalDateTime.now())
                .content(newCommentDto.getContent())
                .writer(seniorUser)
                .article(article)
                .build();
        commentRepository.save(comment);
        seniorUser.createComment(comment);
        article.createComment(comment);
        return count+1;
    }

    public List<Comment> getAllComments(Long id) {
        return commentRepository.findByArticleId(id);
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }

    public void deleteComment(Long id, String name) throws Exception {
        Optional<Comment> oc = commentRepository.findById(id);
        if (oc.isEmpty()) {
            throw new Exception();
        }
        Comment comment = oc.get();
        if(!comment.getWriter().getOauth2Id().equals(name)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"삭제 권한이 없습니다.");
        }
        commentRepository.deleteById(comment.getId());
    }
}
