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

import java.util.List;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    public CommentService(CommentRepository commentRepository, PostRepository postRepository, UserRepository userRepository){
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public List<Comment> getAllComments(){
        try {
            return this.commentRepository.findAll();
        }
        catch(IllegalArgumentException e){
            throw new IllegalArgumentException("댓글 정보를 받아올 수 없습니다");
        }
    }
    
    public Comment findById(Long id){
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("댓글 정보를 찾을 수 없습니다"));
    }
    
    @Transactional
    public void saveComment(Comment comment){
        try {
            commentRepository.save(comment);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("데이터 무결성 위반: 댓글 저장 실패", e);
        } catch (TransactionSystemException e) {
            throw new IllegalStateException("트랜잭션 처리 중 오류 발생", e);
        } catch (Exception e) {
            throw new RuntimeException("예상치 못한 오류로 댓글 작성에 실패했습니다.", e);
        }
    }
    
    @Transactional
    public void deleteComment(Long id, Long userId){
        try{
            Comment comment = commentRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다"));
            Post post = comment.getPost();
            User user = comment.getUser();
            post.deleteComment(comment);
            user.deleteComment(comment);
            postRepository.save(post);
            userRepository.save(user);
            commentRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("데이터 무결성 위반: 댓글 삭제 실패", e);
        } catch (TransactionSystemException e) {
            throw new IllegalStateException("트랜잭션 처리 중 오류 발생", e);
        } catch (Exception e) {
            throw new RuntimeException("예상치 못한 오류로 댓글 삭제에 실패했습니다.", e);
        }
    }
}
