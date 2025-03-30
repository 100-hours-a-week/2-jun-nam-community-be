package hello.hello_spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.hello_spring.model.Post;
import hello.hello_spring.model.PostInteraction;
import hello.hello_spring.model.User;
import hello.hello_spring.repository.PostInteractionRepository;
import hello.hello_spring.repository.PostRepository;
import hello.hello_spring.repository.UserRepository;
import hello.hello_spring.service.PostService;
import hello.hello_spring.service.UserService;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class PostControllerUnitTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired PostRepository postRepository;

    @Autowired
    PostInteractionRepository postInteractionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    private User currentUser;
    private Post currentPost;

    @BeforeEach
    void setUp() {
        User user = new User("test5@example.com", "1!Qqqqqq", "테스터", null, null);
        userService.saveUser(user, false);
        this.currentUser = user;
        Post post = new Post(
                "테스트 제목",
                "테스트 내용",
                user.getNickname(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                0,
                0,
                0,
                user.getId(),
                user.getImageUrl(),
                "/api/images/posts/sample.jpg",
                user
        );
        postRepository.save(post);
        this.currentPost = post;

        PostInteraction interaction = new PostInteraction();
        interaction.setUser(currentUser);
        interaction.setPost(currentPost);
        interaction.setLiked(false); // 초기에는 false
        interaction.setViewed(true);
        interaction.setInteractedAt(LocalDateTime.now());

        postInteractionRepository.save(interaction);
    }

    @Test
    @DisplayName("게시글 작성 성공")
    void write_post_success() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", currentUser);

        Map<String, Object> newPost = new HashMap<>();
        newPost.put("title", "테스트 제목");
        newPost.put("content", "테스트 내용");
        newPost.put("author", currentUser.getNickname());
        newPost.put("createdAt", LocalDateTime.now().toString());
        newPost.put("modifiedAt", LocalDateTime.now().toString());
        newPost.put("likeCount", 0);
        newPost.put("commentCount", 0);
        newPost.put("viewCount", 0);
        newPost.put("comments", new ArrayList<>());
        newPost.put("interactions", new ArrayList<>());
        newPost.put("authorId", currentUser.getId());
        newPost.put("profileImage", currentUser.getImageUrl());
        newPost.put("postImageUrl", "/api/images/posts/sample.jpg");

        mockMvc.perform(post("/posts")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPost)))
                .andExpect(status().isCreated());

    }

    @Test
    @DisplayName("특정 게시글 조회")
    void view_post_detail() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", currentUser);

        mockMvc.perform(get("/api/posts/{id}", currentPost.getId())
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("특정 게시글 수정")
    void edit_post() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", currentUser);

        Map<String, Object> newPost = new HashMap<>();
        newPost.put("title", "새로운 제목");
        newPost.put("content", "새로운 내용");
        newPost.put("author", currentUser.getNickname());
        newPost.put("createdAt", LocalDateTime.now().toString());
        newPost.put("modifiedAt", LocalDateTime.now().toString());
        newPost.put("likeCount", 0);
        newPost.put("commentCount", 0);
        newPost.put("viewCount", 0);
        newPost.put("comments", new ArrayList<>());
        newPost.put("interactions", new ArrayList<>());
        newPost.put("authorId", currentUser.getId());
        newPost.put("profileImage", currentUser.getImageUrl());
        newPost.put("postImageUrl", "/api/images/posts/sample.jpg");

        mockMvc.perform(patch("/posts/{id}", currentPost.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPost)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("새로운 제목"))
                .andExpect(jsonPath("$.content").value("새로운 내용"));
    }

    @Test
    @DisplayName("특정 게시물에 좋아요 반영")
    void like_post() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", currentUser);

        mockMvc.perform(post("/posts/{id}/like", currentPost.getId())
                        .session(session))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("특정 게시물 삭제")
    void delete_post() throws Exception{
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", currentUser);

        mockMvc.perform(delete("/posts/{id}", currentPost.getId())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("게시글이 성공적으로 삭제되었습니다."));
    }
}
