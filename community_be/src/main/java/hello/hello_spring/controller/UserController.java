package hello.hello_spring.controller;

import hello.hello_spring.common.IpRateLimiter;
import hello.hello_spring.common.PreventDuplicateRequest;
import hello.hello_spring.dto.user.UserRequestDTO;
import hello.hello_spring.model.User;
import hello.hello_spring.repository.UserRepository;
import hello.hello_spring.service.UserService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/users")
public class UserController {
    private UserService userService;
    private UserRepository userRepository;

    @Autowired
    private IpRateLimiter ipRateLimiter;
    public UserController(UserService userService, UserRepository userRepository){
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping
    public String signupPage(){
        return "/html/signUp.html";
    }

    @PostMapping
    @PreventDuplicateRequest
    public ResponseEntity<String> signup(@RequestBody UserRequestDTO userDto, HttpServletRequest request) {
        String ip = getClientIp(request);
        Bucket bucket = ipRateLimiter.resolveBucket(ip);

        try {
            User newUser = new User(userDto.getEmail(), userDto.getPassword(), userDto.getNickname(), userDto.getProfileImage(), userDto.getImageUrl());
            userService.saveUser(newUser, false);
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
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id, HttpSession httpSession){
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new IllegalStateException("해당 사용자가 존재하지 않습니다."));

            userService.deleteUser(id);

            return ResponseEntity.ok("회원탈퇴가 처리되었습니다.");
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
    public ResponseEntity<?> editProfile(@PathVariable("id") Long id, @RequestBody UserRequestDTO userDto, HttpSession httpSession){
        try {

            User targetUser = userService.findUserById(id);
            if(userDto.getPassword() != null && !userDto.getPassword().isEmpty())
                targetUser.setPassword(userDto.getPassword());

            if(userDto.getNickname() != null && !userDto.getNickname().isEmpty())
                targetUser.setNickname(userDto.getNickname());

            if(userDto.getProfileImage() != null && !userDto.getProfileImage().isEmpty())
                targetUser.setProfileImage(userDto.getProfileImage());

            userService.saveUser(targetUser, true);
            httpSession.setAttribute("user", targetUser);
            return ResponseEntity.ok(targetUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("페이지를 찾을 수 없습니다");
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        return (xfHeader != null) ? xfHeader.split(",")[0] : request.getRemoteAddr();
    }
}