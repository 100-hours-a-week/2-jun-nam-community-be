package hello.hello_spring.repository;

import hello.hello_spring.model.Comment;
import hello.hello_spring.model.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.post IN :posts")
    void deleteAllByPostIn(@Param("posts") List<Post> posts);

    @Modifying
    @Transactional
    @Query("DELETE FROM PostInteraction pi WHERE pi.post = :post")
    void deleteAllByPost(@Param("post") Post post);
}
