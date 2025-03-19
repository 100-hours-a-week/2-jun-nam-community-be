package hello.hello_spring.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String author;
    private String content;
    private Long onPostId;
    private Long onUserId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", nullable = false) // ✅ FK 설정
    @JsonBackReference(value = "post-comments")
    private Post post;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false) // ✅ FK 설정
    @JsonBackReference(value = "user-comments")
    private User user;

    public Long getOnPostId() {
        return onPostId;
    }

    public void setOnPostId(Long postId) {
        this.onPostId = postId;
    }

    public Long getOnUserId() {
        return onUserId;
    }

    public void setOnUserId(Long userId) {
        this.onUserId = userId;
    }

    public Long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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