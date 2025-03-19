package hello.hello_spring.repository;

import hello.hello_spring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.posts " +
            "LEFT JOIN FETCH u.comments " +
            "LEFT JOIN FETCH u.interactions " +
            "WHERE u.email = :email")
    Optional<User> findByEmailWithDetails(@Param("email") String email);
}
