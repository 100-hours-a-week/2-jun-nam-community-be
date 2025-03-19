package hello.hello_spring.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private String profileImage;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "user-comments")
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "user-posts")
    @JsonIgnore
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "user-interactions")
    @JsonIgnore
    private List<PostInteraction> interactions = new ArrayList<>();

    public User(){

    }
    public User(String email, String password, String nickname, String profileImage){
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setUser(this);
    }

    public void deleteComment(Comment comment){
        this.comments.remove(comment);
        comment.setUser(null);
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void addPost(Post post) {
        this.posts.add(post);
        post.setUser(this);
    }

    public void deletePost(Post post) {
        this.posts.remove(post);
        post.setUser(null);
    }

    public List<PostInteraction> getInteractions() {
        return interactions;
    }

    public void deletePostInteractions(Post post){
        interactions.removeIf(interaction -> interaction.getPost().equals(post));
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
