package hello.hello_spring.dto.post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequestDTO {
    private String title;
    private String content;
    private String postImageUrl;
}
