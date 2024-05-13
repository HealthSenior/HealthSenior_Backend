package out4ider.healthsenior.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import out4ider.healthsenior.domain.Article;
import out4ider.healthsenior.domain.Comment;
import out4ider.healthsenior.domain.SeniorUser;
import out4ider.healthsenior.dto.CommentResponseDto;
import out4ider.healthsenior.dto.NewCommentDto;
import out4ider.healthsenior.service.ArticleService;
import out4ider.healthsenior.service.CommentService;
import out4ider.healthsenior.service.SeniorUserService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    private final SeniorUserService seniorUserService;
    private final ArticleService articleService;

    @GetMapping("/list/{id}")
    public List<CommentResponseDto> getCommentList(@PathVariable Long id) throws Exception {
        Optional<Article> oa = articleService.getArticleById(id);
        if(oa.isEmpty()){
            throw new Exception();
        }
        Article article = oa.get();
        List<Comment> comments= article.getComments();
        List<CommentResponseDto> commentResponseDtos = new ArrayList<>();
        for(Comment comment : comments){
            commentResponseDtos.add(new CommentResponseDto(comment));
        }

        return commentResponseDtos;
    }

    @PostMapping("/create/{id}")
    @Transactional
    public int createComment(@PathVariable Long id, @RequestBody NewCommentDto newCommentDto, Principal principal) throws Exception {
        Optional<Article> oa = articleService.getArticleById(id);
        if(oa.isEmpty()) {
            throw new Exception();
        }
        Article article = oa.get();
        String name = principal.getName();
        Optional<SeniorUser> op = seniorUserService.findByOauth2Id(name);
        if (op.isEmpty()) {
            throw new Exception();
        }
        SeniorUser seniorUser = op.get();
        Comment comment = Comment.builder()
                .createdAt(LocalDateTime.now())
                .content(newCommentDto.getContent())
                .writer(seniorUser)
                .article(article)
                .build();
        commentService.saveComment(comment);
        seniorUser.createComment(comment);
        article.createComment(comment);
        return article.getComments().size();
    }
   /* @PutMapping("/modify/{id}")
    public Comment modifyComment(@PathVariable Long id, @RequestBody NewCommentDto newCommentDto) throws Exception {
        Optional<Comment> oc = this.commentService.getCommentById(id);
        if(oc.isEmpty()) {
            throw new Exception();
        }
        Comment comment = oc.get();
        comment.setContent(newCommentDto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        Comment saved = commentService.saveComment(comment);
        return saved;
    }*/

    @DeleteMapping("/delete/{id}")
    public void deleteComment(@PathVariable("id") Long id, Principal principal) throws Exception {
        Optional<Comment> oc = this.commentService.getCommentById(id);
        if(oc.isEmpty()) {
            throw new Exception();
        }
        Comment comment = oc.get();
        if(!comment.getWriter().getOauth2Id().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"삭제 권한이 없습니다.");
        }
        this.commentService.deleteCommentById(comment.getId());
    }
}
