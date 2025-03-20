package hello.hello_spring.service;

import hello.hello_spring.model.Comment;
import hello.hello_spring.model.Post;
import hello.hello_spring.model.PostInteraction;
import hello.hello_spring.model.User;
import hello.hello_spring.repository.CommentRepository;
import hello.hello_spring.repository.PostInteractionRepository;
import hello.hello_spring.repository.PostRepository;
import hello.hello_spring.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostInteractionRepository postInteractionRepository;
    public PostService(PostRepository postRepository, CommentRepository commentRepository, UserRepository userRepository, PostInteractionRepository postInteractionRepository){
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postInteractionRepository = postInteractionRepository;
    }

    public Post findPostById(Long id){
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
    }

    public List<Post> findAllPost(){
        return postRepository.findAll();
    }

    public void viewPost(Long postId, Long userId){
        Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다"));

        Optional<PostInteraction> interaction = postInteractionRepository.findByPostAndUser(post, user);
        System.out.println("[debug]: asdfasdfasdf");
        if (interaction.isEmpty()) {
            PostInteraction newInteraction = new PostInteraction();
            newInteraction.setPost(post);
            newInteraction.setUser(user);
            newInteraction.setViewed(true);
            postInteractionRepository.save(newInteraction);
        }

        post.setViewCount();
        this.savePost(post);
    }

    public List<Post> findActivePost(){
        return postRepository.findAllActivePosts();
    }

    @Transactional
    public void savePost(Post post){
        if(post.getTitle() == null || post.getTitle().isEmpty()){
            throw new IllegalArgumentException("제목은 공백일 수 없습니다");
        }
        if(post.getTitle() == null || post.getContent().isEmpty()){
            throw new IllegalArgumentException("게시글의 내용은 공백일 수 없습니다");
        }
        postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id, User user){
        try{
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new IllegalStateException("해당 게시글이 존재하지 않습니다."));
            if (!post.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("해당 게시글을 삭제할 권한이 없습니다.");
            }

            if (post.getPostImageUrl() != null && !post.getPostImageUrl().isEmpty()) {
                deletePostImage(post.getPostImageUrl());
            }

            //Soft Delete 적용
            post.softDelete();

            //변경 사항 저장 (UPDATE 실행됨)
            postRepository.save(post);

            cleanupDeletedPosts(user);
            System.out.println("게시글 소프트 삭제 완료");
        }
        catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("데이터 무결성 위반: 게시글 삭제 실패", e);
        } catch (TransactionSystemException e) {
            throw new IllegalStateException("트랜잭션 처리 중 오류 발생", e);
        } catch (Exception e) {
            System.out.println("aasdfa-------------");
            e.printStackTrace();
            System.out.println("aasdfa-------------");
            throw new RuntimeException("예상치 못한 오류로 게시글 삭제에 실패했습니다.", e);
        }
    }

    @Transactional
    public void cleanupDeletedPosts(User user) {
        LocalDateTime cutoffDate = LocalDateTime.now();

        // ✅ 30일이 지난 Soft Delete된 데이터 조회
        List<Post> postsToDelete = postRepository.findAllByDeletedAtBefore(cutoffDate);
        System.out.println("[debug in cleanupDeletedPosts] postsToDelete : " + postsToDelete);
        if (!postsToDelete.isEmpty()) {


            // ✅ 3️⃣ 게시글 삭제
            postRepository.deleteAll(postsToDelete);
            System.out.println(postsToDelete.size() + "개의 삭제된 게시글을 영구적으로 삭제했습니다.");
        }
    }

    @Transactional
    public void setLike(Long id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("해당 게시글이 존재하지 않습니다."));

        User user = post.getUser();

        PostInteraction postInteraction = postInteractionRepository.findByPostAndUser(post, user)
                        .orElseThrow(() -> new IllegalStateException("postInteraction이 존재하지 않습니다."));
        System.out.println("[debug]: asdfasdfasdf");
        System.out.println("[debug]: " + postInteraction.getPost());
        System.out.println("[debug] postInteraction.getLine(): " + postInteraction.getLiked());
        if (postInteraction.getPost() == post) {
           if(!postInteraction.getLiked()){
               postInteraction.setLiked(true);
               post.setLikeCount(post.getLikeCount() + 1);
           }
           else{
               postInteraction.setLiked(false);
               post.setLikeCount(post.getLikeCount() - 1);
           }
            System.out.println("[debug] post.getLikeCount(): " + post.getLikeCount());
           postInteractionRepository.save(postInteraction);
           this.savePost(post);
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


