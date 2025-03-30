package hello.hello_spring;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private MockMultipartFile file;

    @BeforeEach
    void setup() throws IOException {
        file = new MockMultipartFile(
            "file",
            "test-image.jpg",
            "image/jpeg",
            "dummy image content".getBytes()
        );

        Path userUploadDir = Paths.get("uploads/users");
        Path postUploadDir = Paths.get("uploads/posts");
        Files.createDirectories(userUploadDir);
        Files.createDirectories(postUploadDir);

        Path userFilePath = userUploadDir.resolve("test-image.jpg");
        if (!Files.exists(userFilePath)) {
            Files.write(userFilePath, "dummy image content".getBytes());
        }

        Path postFilePath = postUploadDir.resolve("test-image.jpg");
        if (!Files.exists(postFilePath)) {
            Files.write(postFilePath, "dummy image content".getBytes());
        }
    }

    @Test
    @DisplayName("게시글 이미지 등록")
    void add_image_post() throws Exception {
        mockMvc.perform(multipart("/api/images/posts")  // URL 주의!
                .file(file))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/images/posts/test-image.jpg")));
    }

    @Test
    @DisplayName("유저 이미지 등록")
    void add_image_user() throws Exception {
        mockMvc.perform(multipart("/api/images/users")  // URL 주의!
                .file(file))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("/api/images/users/test-image.jpg")));
    }

    @Test
    @DisplayName("게시글 이미지 정보 받아오기")
    void get_image_post() throws Exception{
        mockMvc.perform(get("/api/images/posts/{filename}", "test-image.jpg"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("유저 이미지 정보 받아오기")
    void get_image_user() throws Exception{
        mockMvc.perform(get("/api/images/users/{filename}", "test-image.jpg"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("유저 이미지 삭제")
    void delete_image_user() throws Exception{
        mockMvc.perform(delete("/api/images/users/{filename}", "test-image.jpg"))
            .andExpect(status().isOk())
            .andExpect(content().string("파일이 성공적으로 삭제되었습니다."));
    }

    @Test
    @DisplayName("게시글 이미지 삭제")
    void delete_image_post() throws Exception{
        mockMvc.perform(delete("/api/images/posts/{filename}", "test-image.jpg"))
            .andExpect(status().isOk())
            .andExpect(content().string("파일이 성공적으로 삭제되었습니다."));
    }

    @Test
    @DisplayName("User 이미지 업로드 실패 - 파일 없음")
    void uploadUserImage_fail_noFile() throws Exception {
        mockMvc.perform(multipart("/api/images/users")
                .file(new MockMultipartFile("file", new byte[0]))) // 빈 파일 전달
            .andExpect(status().isBadRequest())
            .andExpect(content().string("파일이 없습니다."));
    }

    @Test
    @DisplayName("User 이미지 업로드 실패 - 파일명 없음")
    void uploadUserImage_fail_noFileName() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "", "image/png", "dummy".getBytes());

        mockMvc.perform(multipart("/api/images/users")
                .file(file))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("파일명이 유효하지 않습니다."));
    }

    @Test
    @DisplayName("Post 이미지 업로드 실패 - 파일 없음")
    void uploadPostImage_fail_noFile() throws Exception {
        mockMvc.perform(multipart("/api/images/posts")
                .file(new MockMultipartFile("file", new byte[0]))) // 빈 파일 전달
            .andExpect(status().isBadRequest())
            .andExpect(content().string("파일이 없습니다."));
    }

    @Test
    @DisplayName("Post 이미지 업로드 실패 - 파일명 없음")
    void uploadPostImage_fail_noFileName() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "", "image/png", "dummy".getBytes());

        mockMvc.perform(multipart("/api/images/posts")
                .file(file))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("파일명이 유효하지 않습니다."));
    }

    @Test
    @DisplayName("User 이미지 삭제 실패 - 파일명 없음")
    void deleteUserImage_fail_noFileName() throws Exception {
        mockMvc.perform(delete("/api/images/users/")) // @PathVariable 이므로 빈 값이 들어올 경우 404
            .andExpect(status().isNotFound());    // 매핑 실패로 404
    }

    @Test
    @DisplayName("User 이미지 삭제 실패 - 파일 없음")
    void deleteUserImage_fail_fileNotFound() throws Exception {
        mockMvc.perform(delete("/api/images/users/non_existent_file.png"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("파일을 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("Post 이미지 삭제 실패 - 파일명 없음")
    void deletePostImage_fail_noFileName() throws Exception {
        mockMvc.perform(delete("/api/images/posts/")) // @PathVariable 이므로 빈 값이 들어올 경우 404
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Post 이미지 삭제 실패 - 파일 없음")
    void deletePostImage_fail_fileNotFound() throws Exception {
        mockMvc.perform(delete("/api/images/posts/non_existent_file.png"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("파일을 찾을 수 없습니다."));
    }


}
