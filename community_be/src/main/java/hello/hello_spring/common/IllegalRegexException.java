package hello.hello_spring.common;

public class IllegalRegexException extends IllegalArgumentException{
    public IllegalRegexException(String message) {
        super(message);
    }
}