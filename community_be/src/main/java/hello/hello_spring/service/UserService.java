package hello.hello_spring.service;

import hello.hello_spring.model.Post;
import hello.hello_spring.repository.PostRepository;
import hello.hello_spring.repository.UserRepository;
import hello.hello_spring.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

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
    public void saveUser(User user){
        if(userRepository.existsByEmail(user.getEmail())){
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        try{
            List<Post> userPosts = postRepository.findByUserId(id);
            for (Post post : userPosts) {
                if (post.getPostImageUrl() != null && !post.getPostImageUrl().isEmpty()) {
                    deletePostImage(post.getPostImageUrl());
                }
            }
            userRepository.deleteById(id);
            System.out.println("유저 삭제 완료");
        }
        catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("데이터 무결성 위반: 유저 정보 삭제 실패", e);
        } catch (TransactionSystemException e) {
            throw new IllegalStateException("트랜잭션 처리 중 오류 발생", e);
        } catch (Exception e) {
            System.out.println("aasdfa-------------");
            e.printStackTrace();
            System.out.println("aasdfa-------------");
            throw new RuntimeException("예상치 못한 오류로 유저 정보 삭제에 실패했습니다.", e);
        }
    }

    private void deletePostImage(String fileUrl) {
        try {
            String baseDirectory = "uploads/posts/"; // 파일이 저장된 기본 폴더
            String fileName = Paths.get(new URI(fileUrl).getPath()).getFileName().toString();
            Path filePath = Paths.get(baseDirectory, fileName);

            // 파일 존재 여부 확인 후 삭제
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("이미지 삭제 완료: " + filePath);
            } else {
                System.out.println("삭제할 파일이 존재하지 않음: " + filePath);
            }
        } catch (Exception e) {
            System.err.println("이미지 삭제 중 오류 발생: " + e.getMessage());
        }
    }
}