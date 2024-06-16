package out4ider.healthsenior.controller;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import out4ider.healthsenior.domain.Comment;
import out4ider.healthsenior.dto.CommentResponseDto;
import out4ider.healthsenior.dto.NewCommentDto;
import out4ider.healthsenior.service.CommentService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

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

    @DeleteMapping("/delete/{id}")
    public void deleteComment(@PathVariable("id") Long id, Principal principal) throws Exception {
        commentService.deleteComment(id, principal.getName());
    }
}
