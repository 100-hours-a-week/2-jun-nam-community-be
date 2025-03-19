package hello.hello_spring.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_interactions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "post_id"}) // 같은 사용자가 같은 게시글에 중복 좋아요 방지
})
public class PostInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonBackReference(value = "post-interactions")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value = "user-interactions")
    private User user;

    private Boolean liked = false;
    private Boolean viewed = false;
    private LocalDateTime interactedAt;

    public PostInteraction() {
        this.interactedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public Boolean getViewed() {
        return viewed;
    }

    public void setViewed(Boolean viewed) {
        this.viewed = viewed;
    }

    public LocalDateTime getInteractedAt() {
        return interactedAt;
    }

    public void setInteractedAt(LocalDateTime interactedAt) {
        this.interactedAt = interactedAt;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
