package hello.hello_spring.controller;

import hello.hello_spring.model.Auth;
import hello.hello_spring.model.User;
import hello.hello_spring.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }
    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> getUserData(@RequestBody Auth auth, HttpSession session){
        try{
            User user = userService.findUser(auth.getEmail(), auth.getPassword());
            if (user != null) {
                session.setAttribute("user", user);
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(URI.create("/html/index.html"));  // Redirect to index.html
                return new ResponseEntity<>(headers, HttpStatus.OK);
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("등록되지 않은 사용자입니다");
            }
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/login")
    public String returnIndexPage(){
        return "redirect:/index";
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserSession(HttpSession session){
        User user = (User)session.getAttribute("user");

        if(user == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 정보 없음");
        }

        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // 세션 무효화 (로그아웃)
        return ResponseEntity.ok("로그아웃되었습니다.");
    }
}
