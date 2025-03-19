package hello.hello_spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    @GetMapping("/")
    public String loginPage() {
        return "/html/login.html";
    }

    @GetMapping("/index")
    public String indexPage() {
        return "/html/index.html";
    }

    @GetMapping("/posts/write")
    public String createPostPage() {
        System.out.println("hi");
        return "/html/createPost.html";
    }

}
