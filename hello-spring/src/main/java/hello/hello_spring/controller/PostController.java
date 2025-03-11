package hello.hello_spring.controller;

import hello.hello_spring.model.Post;
import hello.hello_spring.model.User;
import hello.hello_spring.repository.PostRepository;
import hello.hello_spring.repository.UserRepository;
import hello.hello_spring.service.PostService;
import hello.hello_spring.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@Controller
public class PostController {
    private final PostService postService;
    private final PostRepository postRepository;
    public PostController(PostService postService, PostRepository postRepository){
        this.postService = postService;
        this.postRepository = postRepository;
    }

    @PostMapping("/posts")
    @ResponseBody
    public ResponseEntity<?> createPost(@RequestBody Post post){
        try{
            postService.savePost(post);

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create("/html/index.html"));  // Redirect to index.html

            return new ResponseEntity<>(headers, HttpStatus.CREATED);
        }
        catch (IllegalArgumentException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        }
        catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("예상치 못한 에러가 발생했습니다");
        }
    }

    @GetMapping("/api/posts/{id}")
    public ResponseEntity<?> getPostById(@PathVariable("id") Long id) {
        try {
            Post post = postService.findPostById(id);
            return ResponseEntity.ok(post);  // ✅ JSON 응답 반환
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("게시글을 찾을 수 없습니다.");
        }
    }

    @GetMapping("/posts/{id}/edit")
    public String editPost(@PathVariable("id") Long id) {
        try {
            return "/html/editPost.html";
        } catch (IllegalArgumentException e) {
            return "/html/index.html";
        }
    }

    @PatchMapping("/posts/{id}")
    @ResponseBody
    public ResponseEntity<?> patchPost(@PathVariable("id") Long id, @RequestBody Post post){
        try {
            System.out.println(post.getTitle() + " " + post.getContent());
            Post targetPost = postService.findPostById(id);

            targetPost.setTitle(post.getTitle());
            targetPost.setContent(post.getContent());
            postRepository.save(targetPost);
            return ResponseEntity.ok(targetPost);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("페이지를 찾을 수 없습니다");
        }
    }

    @GetMapping("/posts/{id}")
    public String getPostDetail(@PathVariable("id") Long id) {
        try {
            Post post = postService.findPostById(id);
            post.updateViewCount();
            post.updateCommentCount();
            postRepository.save(post);
            return "/html/post.html";
        } catch (IllegalArgumentException e) {
            return "/html/index.html";
        }
    }

    @GetMapping("/posts")
    @ResponseBody
    public List<Post> getPosts(){
        List<Post> posts = postService.findAllPost();

        if(posts.isEmpty()){
            System.out.println("no posts yet");
        }
        return posts;
    }

    @DeleteMapping("/posts/{id}")
    @ResponseBody
    public ResponseEntity<?> deletePost(@PathVariable Long id, HttpSession httpSession){
        try {
            System.out.println("[Debug]: httpSession: " + httpSession);
            User user = (User)httpSession.getAttribute("user");
            postService.deletePost(id, user.getId());

            return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
        }
        catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류로 인해 게시글 삭제에 실패했습니다.");
        }
    }
}
