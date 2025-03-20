package hello.hello_spring.repository;

import hello.hello_spring.model.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL")
    List<Post> findAllActivePosts();


    @Modifying
    @Transactional
    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NOT NULL AND p.deletedAt < :cutoffDate")
    List<Post> findAllByDeletedAtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
}

