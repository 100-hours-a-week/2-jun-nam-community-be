package hello.hello_spring.service;

import hello.hello_spring.common.IllegalRegexException;
import hello.hello_spring.model.Post;
import hello.hello_spring.repository.PostRepository;
import hello.hello_spring.repository.UserRepository;
import hello.hello_spring.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    public UserService(UserRepository userRepository, PostRepository postRepository){
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public User findUserById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다"));
    }

    @Transactional(readOnly = true)
    public User findUser(String email, String password){
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다"));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        return user;
    }

    @Transactional
    public void saveUser(User user, boolean isPasswordChange){
        String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        if (!Pattern.matches(emailRegex, user.getEmail())) {
            throw new IllegalRegexException("유효하지 않은 이메일 형식입니다.");
        }

        // 비밀번호 유효성 검사
        if (isPasswordChange && user.getPassword() != null) {
            // 비밀번호 유효성 검사
            String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$";
            if (!Pattern.matches(passwordRegex, user.getPassword())) {
                throw new IllegalRegexException("비밀번호는 8~20자 사이이며, 대소문자/숫자/특수문자를 포함해야 합니다.");
            }

            if(user.getPassword().length() < 8 || user.getPassword().length() > 20){
                throw new IllegalRegexException("비밀번호는 8자 이상, 20자 이하여야 합니다.");
            }
        }

        Optional<User> existing = userRepository.findByEmail(user.getEmail());

        if(existing.isPresent() && (user.getId() == null || !existing.get().getId().equals(user.getId()))){
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            // unique 제약 조건 위반 등 → 동시성 충돌 시 처리
            throw new IllegalArgumentException("요청을 처리하는 중 오류가 발생했습니다. 다시 시도해 주세요.");
        }
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("등록되지 않은 이메일입니다");
        }

        try {
            List<Post> userPosts = postRepository.findByUserId(id);
            for (Post post : userPosts) {
                if (post.getPostImageUrl() != null && !post.getPostImageUrl().isEmpty() && !post.getPostImageUrl().equals("/api/images/posts/default.jpg")) {
                    deletePostImage(post.getPostImageUrl());
                }
            }
            userRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("데이터 무결성 위반: 유저 정보 삭제 실패", e);
        } catch (TransactionSystemException e) {
            throw new IllegalStateException("트랜잭션 처리 중 오류 발생", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("예상치 못한 오류로 유저 정보 삭제에 실패했습니다.", e);
        }
    }


    private void deletePostImage(String fileUrl) throws IOException{
        try {
            List<String> defaultImages = List.of("default.jpg", "default_profile.jpg");
            String baseDirectory = "uploads/posts/"; // 파일이 저장된 기본 폴더
            String fileName = Paths.get(new URI(fileUrl).getPath()).getFileName().toString();
            Path filePath = Paths.get(baseDirectory, fileName);

            // 파일 존재 여부 확인 후 삭제
            if (Files.exists(filePath) && !defaultImages.contains(fileName)) {
                Files.delete(filePath);
            }
        }
        catch (URISyntaxException e) {
            throw new IOException("파일 URL 구문 오류: " + fileUrl, e);
        }
        catch (IOException e) {
            throw new IOException("게시글 이미지 삭제 실패: " + fileUrl, e);
        }
    }
}