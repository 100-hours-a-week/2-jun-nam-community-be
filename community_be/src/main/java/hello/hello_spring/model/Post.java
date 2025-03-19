package hello.hello_spring.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long postId;
    private String title;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Integer likeCount;
    private Integer commentCount;
    private Integer viewCount;
    private Long authorId;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // NULL이면 삭제되지 않은 상태

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "post-comments")
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value = "user-posts")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "post-interactions")
    private List<PostInteraction> interactions = new ArrayList<>();

    public void softDelete() {
        this.deletedAt = LocalDateTime.now(); // 현재 시간으로 설정하여 논리적 삭제
    }

    public Long getId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount() {
        this.viewCount = (int) interactions.stream().filter(PostInteraction::getViewed).count();
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setPost(this);
    }

    public void deleteComment(Comment comment){
        this.comments.remove(comment);
        comment.setPost(null);
    }

    public List<PostInteraction> getInteractions() {
        return interactions;
    }

    public void setInteractions(List<PostInteraction> interactions) {
        this.interactions = interactions;
    }

    public void updateCommentCount(){
        this.commentCount = this.comments.size();
    }

    public void updateViewCount(){
        this.viewCount = this.viewCount + 1;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAuthorId(Long id){
        System.out.println("[debug - author Id] : " + id);
        this.authorId = id;
    }

    public Long getAuthorId(){
        return this.authorId;
    }
}
