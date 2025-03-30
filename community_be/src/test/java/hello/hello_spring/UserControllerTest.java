package hello.hello_spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.hello_spring.model.User;
import hello.hello_spring.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private User savedUser;

	@BeforeEach
	void setUp() {
		User user = new User("test5@example.com", "1!Qqqqqq", "테스터", null, null);
		savedUser = userRepository.save(user);
	}


	@Test
	@DisplayName("회원가입 성공")
	void signup_success() throws Exception {
		User requestUser = new User("newuser@example.com", "1!Qqqqqq", "뉴유저", null, null);
		savedUser = requestUser;
		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestUser)))
				.andExpect(status().isOk())
				.andExpect(content().string("회원가입 성공"));
	}

	@Test
	@DisplayName("중복 이메일로 회원가입 시도")
	void signup_duplicateEmail() throws Exception {
		User duplicateUser = new User("test5@example.com", "1!Qqqqqq", "중복유저", null, null);

		mockMvc.perform(post("/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(duplicateUser)))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("이미 등록된 이메일입니다."));
	}

	@Test
	@DisplayName("프로필 수정 성공")
	@Transactional
	void editProfile_success() throws Exception {
		System.out.println("savedUser id: " + savedUser.getId() + " saveUser email: " + savedUser.getEmail());
		User updatedInfo = new User(null, "1!Qqqqqq", "새닉네임", null, null);
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("user", savedUser);

		mockMvc.perform(patch("/users/{id}", savedUser.getId())
						.session(session)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updatedInfo)))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nickname").value("새닉네임"));
	}

	@Test
	@DisplayName("회원 삭제 성공")
	@Transactional
	void deleteUser_success() throws Exception {
		mockMvc.perform(delete("/users/{id}", savedUser.getId()))
				.andExpect(status().isOk())
				.andExpect(content().string("회원탈퇴가 처리되었습니다."));
	}

	@Test
	@DisplayName("존재하지 않는 회원 삭제 시도")
	void deleteUser_notFound() throws Exception {
		mockMvc.perform(delete("/users/{id}", 999L))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("해당 사용자가 존재하지 않습니다."));
	}

	@Test
	@DisplayName("프로필 수정 - 존재하지 않는 사용자")
	void editProfile_userNotFound() throws Exception {
		User updatedInfo = new User(null, "newpass", "새닉네임", "image.jpg", null);

		mockMvc.perform(patch("/users/{id}", 9999L)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(updatedInfo)))
				.andExpect(status().isNotFound())
				.andExpect(content().string("페이지를 찾을 수 없습니다"));
	}
}
