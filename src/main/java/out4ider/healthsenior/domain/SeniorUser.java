package out4ider.healthsenior.domain;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import out4ider.healthsenior.enums.Role;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeniorUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long userId;
    @OneToMany(mappedBy = "seniorUser", fetch = FetchType.LAZY)
    List<CommunityChatRelation> communityChatRelation;
    String userName;
    String email;
    Integer userAge;
    boolean isMale;
    String oauth2Id;
    Role role;
    @OneToMany(mappedBy = "writer", fetch = FetchType.LAZY)
    List<Article> articleList;

    @OneToMany(mappedBy = "writer", fetch = FetchType.LAZY)
    List<Comment> commentList;

    @OneToMany(mappedBy = "seniorUser", fetch = FetchType.LAZY)
    private List<LikeUserRelation> likeUserRelations;

    public void joinChat(CommunityChatRelation communityChatRelation){
        this.communityChatRelation.add(communityChatRelation);
    }

    @Transactional
    public void createArticle(Article article){
        this.articleList.add(article);
    }
    @Transactional
    public void createComment(Comment comment){
        this.commentList.add(comment);
    }
    @Transactional
    public void createLikeUserRelation(LikeUserRelation likeUserRelation) {
        this.likeUserRelations.add(likeUserRelation);
    }
}
