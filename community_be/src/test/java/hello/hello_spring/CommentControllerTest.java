package hello.hello_spring;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.hello_spring.dto.comment.CommentRequestDTO;
import hello.hello_spring.model.Comment;
import hello.hello_spring.model.Post;
import hello.hello_spring.model.User;
import hello.hello_spring.repository.CommentRepository;
import hello.hello_spring.repository.PostRepository;
import hello.hello_spring.repository.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private User currentUser;

    private Post currentPost;

    private Comment createdComment;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MockHttpSession currentSession;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        userRepository.deleteAll(); // or nativeQuery 로 truncate 해도 됨
        postRepository.deleteAll();

        // 1. user 생성 및 save → ID 확정
        User user = new User("test@example.com", "1!Qqqqqq", "nickname", null, null);
        user = userRepository.save(user);
        this.currentUser = user;

        // 2. post 생성 (user가 저장된 상태이므로 id 정상)
        Post post = new Post(
            "테스트 제목",
            "내용",
            user.getNickname(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            0, 0, 0,
            user.getId(),
            null,
            null,
            user
        );
        post = postRepository.save(post);
        this.currentPost = post;

        // 3. session 설정
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("user", user);
        this.currentSession = mockHttpSession;

        // 4. comment 준비 (실제 save는 필요하면 각각 테스트 내에서)
        Comment comment = Comment.builder()
            .author("testUser")
            .content("테스트 댓글")
            .onPostId(post.getId())
            .onUserId(user.getId())
            .modifiedAt(LocalDateTime.now())
            .post(post)
            .user(user)
            .build();

        createdComment = commentRepository.save(comment);
    }


    @Test
    @DisplayName("댓글 등록")
    void create_comment() throws Exception{
        CommentRequestDTO commentRequest = new CommentRequestDTO();
        commentRequest.setContent("테스트 댓글");
        commentRequest.setOnPostId(currentPost.getId());
        commentRequest.setOnUserId(currentUser.getId());

        mockMvc.perform(post("/comments")
                .session(currentSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string("댓글이 성공적으로 작성되었습니다."));
    }

    @Test
    @DisplayName("댓글 삭제")
    void delete_comment() throws Exception{
        mockMvc.perform(delete("/comments/{id}", createdComment.getId())
                .session(currentSession))
            .andExpect(status().isOk())
            .andExpect(content().string("댓글이 성공적으로 삭제되었습니다."));
    }

    @Test
    @DisplayName("댓글 수정")
    void modify_comment() throws Exception{
        CommentRequestDTO commentRequest = new CommentRequestDTO();
        commentRequest.setContent("테스트 댓글");
        commentRequest.setOnPostId(currentPost.getId());
        commentRequest.setOnUserId(currentUser.getId());

        mockMvc.perform(patch("/comments/{id}", createdComment.getId())
                .session(currentSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentRequest)))
            .andExpect(status().isOk())
            .andExpect(content().string("댓글이 성공적으로 수정되었습니다."));
    }

    @Test
    @DisplayName("모든 댓글 정보 가져오기")
    void get_all_comments() throws Exception{
        mockMvc.perform(get("/comments"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("모든 댓글 가져오기 - 댓글 없음")
    void getAllComments_empty() throws Exception {
        commentRepository.deleteAll(); // 댓글 전부 삭제

        mockMvc.perform(get("/comments")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().json("[]")); // 빈 배열 체크
    }
}
