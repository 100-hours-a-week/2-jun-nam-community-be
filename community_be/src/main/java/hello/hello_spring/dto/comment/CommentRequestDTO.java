package hello.hello_spring.dto.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDTO {
    private String content;
    private Long onPostId;
    private Long onUserId;
}
