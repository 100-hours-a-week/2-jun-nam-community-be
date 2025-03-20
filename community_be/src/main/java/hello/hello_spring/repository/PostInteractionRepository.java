package hello.hello_spring.repository;

import hello.hello_spring.model.Post;
import hello.hello_spring.model.PostInteraction;
import hello.hello_spring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostInteractionRepository extends JpaRepository<PostInteraction, Long> {
    Optional<PostInteraction> findByPostAndUser(Post post, User user);
}