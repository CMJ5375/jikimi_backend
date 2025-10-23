package code.project.util;

public class CustomJWTException extends RuntimeException{

    public CustomJWTException(String message) {
        super(message);
    }

    // ✅ 이 생성자를 추가하세요!
    public CustomJWTException(String message, Throwable cause) {
        super(message, cause);
    }
}
