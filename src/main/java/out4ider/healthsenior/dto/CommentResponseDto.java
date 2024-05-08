package out4ider.healthsenior.dto;

import lombok.Getter;
import lombok.Setter;
import out4ider.healthsenior.domain.Comment;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseDto {
    private Long id;
    private String content;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.username =comment.getWriter().getUserName();
    }
}
