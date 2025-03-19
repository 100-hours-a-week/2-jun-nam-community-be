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
    public void deletePost(Long id, Long userId){
        try{
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new IllegalStateException("해당 게시글이 존재하지 않습니다."));
            User user = post.getUser();

            // 1️⃣ 연관된 Comments 삭제
            System.out.println("[debug post get comments]: "+ post.getComments());
            System.out.println("[debug post get interactions]: "+ post.getInteractions());
            post.getComments().clear();
//
//            // 2️⃣ 연관된 PostInteractions 삭제
            postInteractionRepository.deleteByPostId(id);
//
            System.out.println(post.getId());
//            user.getComments().removeIf(comment -> comment.getPost().getId().equals(post.getId()));
            user.getInteractions().clear();
            user.getInteractions().removeIf(interaction -> interaction.getPost().equals(post));
            postRepository.delete(post);
            user.deletePost(post);
            System.out.println("[debug]: 123456789");
//
//
//            postRepository.delete(post);
            System.out.println("게시글 삭제 완료");
//
//            postRepository.flush();
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
}
