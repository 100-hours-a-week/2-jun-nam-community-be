package hello.hello_spring.repository;

import hello.hello_spring.model.Post;
import hello.hello_spring.model.PostInteraction;
import hello.hello_spring.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostInteractionRepository extends JpaRepository<PostInteraction, Long> {
    Optional<PostInteraction> findByPostAndUser(Post post, User user);

    @Modifying
    @Query("DELETE FROM PostInteraction pi WHERE pi.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.post IN :posts")
    void deleteAllByPostIn(@Param("posts") List<Post> posts);

}