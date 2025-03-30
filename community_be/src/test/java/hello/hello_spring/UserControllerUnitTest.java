package hello.hello_spring;

import hello.hello_spring.controller.UserController;
import hello.hello_spring.model.User;
import hello.hello_spring.repository.UserRepository;
import hello.hello_spring.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Transactional
@ActiveProfiles("test")
public class UserControllerUnitTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserController userController;

    private final RestTemplate restTemplate = new RestTemplate();

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8080")
            .build();

    @Test
    @DisplayName("동일 IP, 동일 데이터, 동시 요청")
    void 동시에_중복_회원가입_요청시_하나만_저장되어야_한다() throws InterruptedException {
        int threadCount = 5000;
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        String requestBody = """
                {
                    "email": "test3@example.com",
                    "password": "1!Qqqqqq",
                    "nickname": "동시성테스트"
                }
                """;

        for (int i = 0; i < threadCount; i++) {
            webClient.post()
                    .uri("/users")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(requestBody)
                    .exchangeToMono(response -> {
                        HttpStatusCode statusCode = response.statusCode();
                        HttpStatus status = HttpStatus.valueOf(statusCode.value());
                        if (status.is2xxSuccessful()) {
                            successCount.incrementAndGet();
                        } else if (status == HttpStatus.PROCESSING || status == HttpStatus.TOO_MANY_REQUESTS) {
                            failCount.incrementAndGet(); // 정확하게 처리됨
                        } else {
                            failCount.incrementAndGet(); // 예외 응답도 실패 처리
                        }
                        latch.countDown();
                        return response.releaseBody();
                    })
                    .doOnError(err -> {
                        failCount.incrementAndGet();
                        latch.countDown();
                    })
                    .subscribe();
        }

        latch.await();

        userRepository.flush();
        long saved = userRepository.countByEmail("test3@example.com");

        List<User> all = userRepository.findAll();
        System.out.println("size: " + all.size());
        for (User u : all) {
            System.out.println("이메일: [" + u.getEmail() + "]");
        }

        System.out.println("성공 요청 수: " + successCount.get());
        System.out.println("실패 요청 수: " + failCount.get());
        System.out.println("DB 저장 수: " + saved);

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(threadCount - 1);
    }

    @Test
    @DisplayName("다른 IP, 동일 데이터, 동시 요청")
    void diffIpSameDataConcurrent() throws InterruptedException {
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        String requestBody = """
                {
                    "email": "test4@example.com",
                    "password": "1!Qqqqqq",
                    "nickname": "동시성테스트_다른아이피"
                }
                """;

        for (int i = 0; i < threadCount; i++) {
            String fakeIp = (i % 2 == 0) ? "1.2.3.4" : "5.6.7.8"; // IP 두 개 번갈아 사용

            webClient.post()
                    .uri("/users")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("X-Forwarded-For", fakeIp)
                    .bodyValue(requestBody)
                    .exchangeToMono(response -> {
                        HttpStatus status = HttpStatus.valueOf(response.statusCode().value());
                        if (status.is2xxSuccessful()) {
                            successCount.incrementAndGet();
                        } else if (status == HttpStatus.PROCESSING || status == HttpStatus.TOO_MANY_REQUESTS) {
                            failCount.incrementAndGet();
                        } else {
                            failCount.incrementAndGet();
                        }
                        latch.countDown();
                        return response.releaseBody();
                    })
                    .doOnError(err -> {
                        failCount.incrementAndGet();
                        latch.countDown();
                    })
                    .subscribe();
        }

        latch.await();

        userRepository.flush();
        long saved = userRepository.countByEmail("test4@example.com");

        List<User> all = userRepository.findAll();
        System.out.println("size: " + all.size());
        for (User u : all) {
            System.out.println("이메일: [" + u.getEmail() + "]");
        }

        System.out.println("성공 요청 수: " + successCount.get());
        System.out.println("실패 요청 수: " + failCount.get());
        System.out.println("DB 저장 수: " + saved);

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(threadCount - 1);
    }

    @Test
    @DisplayName("다른 IP, 1번째 데이터만 다르고 나머지는 동일 데이터, 동시 요청")
    void 다른IP_두번째부터_9번째데이터는_동일() throws InterruptedException{
        int threadCount = 10;
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        String requestBody = """
                {
                    "email": "test5@example.com",
                    "password": "1!Qqqqqq",
                    "nickname": "동시성테스트_2~9번째 같음"
                }
                """;
        String requestBody2 = """
                {
                    "email": "test6@example.com",
                    "password": "1!Qqqqqq",
                    "nickname": "동시성테스트_2~9번째 같음"
                }
                """;
        for (int i = 0; i < threadCount; i++) {
            String fakeIp = (i % 2 == 0) ? "1.2.3.4" : "5.6.7.8";
            String requestBodyToSend = (i == 0) ? requestBody : requestBody2;

            webClient.post()
                    .uri("/users")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("X-Forwarded-For", fakeIp)
                    .bodyValue(requestBodyToSend)
                    .exchangeToMono(response -> {
                        HttpStatus status = HttpStatus.valueOf(response.statusCode().value());
                        if (status.is2xxSuccessful()) {
                            successCount.incrementAndGet();
                        } else {
                            failCount.incrementAndGet();
                        }
                        return response.releaseBody();
                    })
                    .doOnError(err -> {
                        failCount.incrementAndGet();
                    })
                    .doFinally(signal -> latch.countDown()) // 응답 완료 시점까지 latch 감소
                    .subscribe();
        }

        latch.await(); // 모든 요청 끝날 때까지 대기

        userRepository.flush();
        long saved = userRepository.countByEmail("test5@example.com") | userRepository.countByEmail("test6@example.com");

        List<User> all = userRepository.findAll();
        System.out.println("size: " + all.size());
        for (User u : all) {
            System.out.println("이메일: [" + u.getEmail() + "]");
        }

        System.out.println("성공 요청 수: " + successCount.get());
        System.out.println("실패 요청 수: " + failCount.get());
        System.out.println("DB 저장 수: " + saved);

        assertThat(saved).isEqualTo(1);
    }
}
