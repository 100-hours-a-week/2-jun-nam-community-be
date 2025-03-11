package hello.hello_spring.service;

import hello.hello_spring.model.Comment;
import hello.hello_spring.model.Post;
import hello.hello_spring.model.User;
import hello.hello_spring.repository.CommentRepository;
import hello.hello_spring.repository.PostRepository;
import hello.hello_spring.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    public PostService(PostRepository postRepository, CommentRepository commentRepository, UserRepository userRepository){
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public Post findPostById(Long id){
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
    }

    public List<Post> findAllPost(){
        return postRepository.findAll();
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

            System.out.println(post.getId());
//            user.getComments().removeIf(comment -> comment.getPost().getId().equals(post.getId()));
            user.deletePost(post);
            System.out.println(1);

            postRepository.delete(post);
            System.out.println("게시글 삭제 완료");
        }
        catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("데이터 무결성 위반: 게시글 삭제 실패", e);
        } catch (TransactionSystemException e) {
            throw new IllegalStateException("트랜잭션 처리 중 오류 발생", e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("예상치 못한 오류로 게시글 삭제에 실패했습니다.", e);
        }
    }
}
