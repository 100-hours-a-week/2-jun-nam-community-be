package hello.hello_spring.common;

public class RateLimitException extends IllegalStateException {
    public RateLimitException(String message) {
        super(message);
    }
}