package out4ider.healthsenior.dto;

import lombok.Getter;
import lombok.Setter;
import out4ider.healthsenior.domain.Article;
import out4ider.healthsenior.domain.Comment;
import out4ider.healthsenior.domain.CommunityChatRelation;
import out4ider.healthsenior.domain.CommunityChatRoom;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ArticleResponseDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String username;
    private int commentCount;
    private int likeCount;

    public ArticleResponseDto(Article article) {
        this.commentCount = article.getComments().size();
        this.likeCount = article.getLikeUserRelations().size();
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.createdAt = article.getCreatedAt();
        this.updatedAt = article.getUpdatedAt();
        this.username = article.getWriter().getUserName();
    }
}
