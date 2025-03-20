package hello.hello_spring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hello.hello_spring.model.Post;
import hello.hello_spring.model.User;
import hello.hello_spring.repository.UserRepository;
import hello.hello_spring.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
@RequestMapping(value = "/users")
public class UserController {
    private UserService userService;
    private UserRepository userRepository;
    public UserController(UserService userService, UserRepository userRepository){
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping
    public String signupPage(){
        return "/html/signUp.html";
    }

    @PostMapping
    public ResponseEntity<String> signup(@RequestBody User user) {
        System.out.println(user);
        System.out.println(user.getEmail());
        try {
            User newUser = new User(user.getEmail(), user.getPassword(), user.getNickname(), user.getProfileImage(), user.getImageUrl());
            userService.saveUser(newUser);
            return ResponseEntity.ok("회원가입 성공");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("이미 등록된 이메일입니다.")) {
                return ResponseEntity.badRequest().body("이미 등록된 이메일입니다.");
            } else {
                return ResponseEntity.badRequest().body("회원가입에 실패했습니다.");
            }
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpSession httpSession){
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalStateException("해당 사용자가 존재하지 않습니다."));

            userService.deleteUser(id);

            return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
        }
        catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류로 인해 게시글 삭제에 실패했습니다.");
        }
    }

    @GetMapping("/{id}/edit")
    public String editProfilePage(@PathVariable("id") Long id){
        return "/html/editProfile.html";
    }

    @GetMapping("/{id}/password")
    public String editPasswordPage(@PathVariable("id") Long id){
        return "/html/editPassword.html";
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> editProfile(@PathVariable Long id, @RequestBody User user, HttpSession httpSession){
        try {

            User targetUser = userService.findUserById(id);
            targetUser.setPassword(user.getPassword());
            targetUser.setNickname(user.getNickname());
            targetUser.setProfileImage(user.getProfileImage());
            userRepository.save(targetUser);
            httpSession.setAttribute("user", targetUser);
            return ResponseEntity.ok(targetUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("페이지를 찾을 수 없습니다");
        }
    }
}