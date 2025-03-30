package hello.hello_spring;

import hello.hello_spring.common.IllegalRegexException;
import hello.hello_spring.model.User;
import hello.hello_spring.repository.PostRepository;
import hello.hello_spring.repository.UserRepository;
import hello.hello_spring.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository = mock(UserRepository.class);
    private PostRepository postRepository = mock(PostRepository.class);
    private AtomicInteger existsCallCounter = mock(AtomicInteger.class);

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, postRepository);
    }

    @Test
    @DisplayName("새로운 유저인 경우 DB에 저장되며 save()가 호출된다 - fair case")
    void signup_success(){
        User user = new User("save@example.com", "1!Qqqqqqq", "테스트", null, null);
        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(false);


        userService.saveUser(user, false);

        verify(userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("새로운 유저인 경우 DB에 저장되며 save()가 호출된다 - illegal case")
    void signup_fail_case_illegal_data(){
        User user = new User("save_fail", "1!Qqqqqq", "테스트", null, null);
        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(false);

        IllegalRegexException ex = assertThrows(IllegalRegexException.class, () -> {
            userService.saveUser(user, false);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("중복 이메일인 경우 유저가 DB에 저장되지 않으며 save()가 호출되지 않는다")
    void signup_fail_case_replicate(){
        User user = new User("save@example.com", "1!Qqqqqq", "테스트", null, null);
        // 실제 서비스는 findByEmail() 사용함
        when(userRepository.findByEmail(user.getEmail()))
            .thenReturn(Optional.of(new User("save@example.com", "1!Qqqqqq", "테스트", null, null)));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.saveUser(user, false);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("유저 삭제 - fair case")
    void user_delete_success(){
        Long id = 1L;

        when(userRepository.existsById(id)).thenReturn(true);

        userService.deleteUser(id);

        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("유저 삭제 - illegal case")
    void user_delete_fail(){
        User user = new User("1@example.com", "1!Qqqqqqq", "nickname", null, null);

        when(userRepository.findByEmail("1@example.com"))
                .thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(user.getId());
        });

        verify(userRepository, never()).deleteById(any());
    }
}
