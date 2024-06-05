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
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    private final SeniorUserService seniorUserService;
    private final ArticleService articleService;

    @GetMapping("/list/{id}")
    public List<CommentResponseDto> getCommentList(@PathVariable Long id) throws Exception {
        List<Comment> comments = commentService.getAllComments(id);
        return comments.stream().map(Comment::toResponseDto).collect(Collectors.toList());
    }

    @PostMapping("/create/{id}")
    @Transactional
    public int createComment(@PathVariable Long id, @RequestBody NewCommentDto newCommentDto, Principal principal) throws Exception {
        return commentService.saveComment(id, newCommentDto, principal.getName());
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
        commentService.deleteComment(id, principal.getName());
    }
}
