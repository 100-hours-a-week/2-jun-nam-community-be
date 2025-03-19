package hello.hello_spring.controller;

import hello.hello_spring.model.Comment;
import hello.hello_spring.model.Post;
import hello.hello_spring.model.User;
import hello.hello_spring.service.CommentService;
import hello.hello_spring.service.PostService;
import hello.hello_spring.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value="/comments")
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;
    public CommentController (CommentService commentService, PostService postService, UserService userService){
        this.postService = postService;
        this.commentService = commentService;
        this.userService = userService;
    }
    @GetMapping
    public ResponseEntity<?> getAllComments(){
        try{
            List<Comment> allComments = commentService.getAllComments();

            if (allComments.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList()); // 빈 배열 []
            }

            return ResponseEntity.ok(allComments);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody Comment comment, HttpSession httpSession){
        try {
            System.out.println("createComment: " + comment.getOnPostId());
            Post post = postService.findPostById(comment.getOnPostId());


            User user = userService.findUserById(comment.getOnUserId());
            post.addComment(comment);
            user.addComment(comment);
            commentService.saveComment(comment);

            return ResponseEntity.ok("댓글이 성공적으로 작성되었습니다.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류로 인해 댓글 작성이 실패했습니다.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, HttpSession httpSession){
        try{
            Long userId = (Long)httpSession.getAttribute("id");
            commentService.deleteComment(id, userId);

            return ResponseEntity.ok("댓글이 성공적으로 삭제되었습니다.");
        }
        catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류로 인해 댓글 삭제에 실패했습니다.");
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> modify(@PathVariable Long id, @RequestBody Comment comment){
        try{
            Comment targetComment = commentService.findById(id);
            targetComment.setContent(comment.getContent());
            commentService.saveComment(targetComment);
            return ResponseEntity.ok("댓글이 성공적으로 수정되었습니다.");
        }
        catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류로 인해 댓글 수정에 실패했습니다.");
        }
    }

}