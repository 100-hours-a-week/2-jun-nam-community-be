package hello.hello_spring;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.hello_spring.dto.auth.AuthRequestDTO;
import hello.hello_spring.model.User;
import hello.hello_spring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private UserRepository userRepository;

    private User savedUser;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup(){
        userRepository.deleteAll();

        User user = new User("test@example.com", "1!Qqqqqq", "nickname", null, null);
        userRepository.save(user);
        this.savedUser = user;
    }

    @Test
    @DisplayName("유효한 로그인")
    void login_success() throws Exception{
        AuthRequestDTO loginRequest = new AuthRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("1!Qqqqqq");

        MockHttpSession mockHttpSession = new MockHttpSession();

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .session(mockHttpSession)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유효하지 않은 로그인")
    void login_fail() throws Exception{
        AuthRequestDTO loginRequest = new AuthRequestDTO();
        loginRequest.setEmail("test1@example.com");
        loginRequest.setPassword("1!Qqqqqq");

        MockHttpSession mockHttpSession = new MockHttpSession();

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .session(mockHttpSession)
                .content(objectMapper.writeValueAsString(loginRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인한 유저 정보 반환")
    void return_userinfo() throws Exception{
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", savedUser);

        mockMvc.perform(get("/auth/me")
                .contentType(MediaType.APPLICATION_JSON)
                .session(mockHttpSession))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인한 유저 정보 반환")
    void logout() throws Exception{
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", savedUser);

        mockMvc.perform(post("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .session(mockHttpSession))
            .andExpect(status().isOk())
            .andExpect(content().string("로그아웃되었습니다."));
    }
}
