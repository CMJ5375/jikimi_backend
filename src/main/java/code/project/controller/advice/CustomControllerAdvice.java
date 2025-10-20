package code.project.controller.advice;

import code.project.util.CustomJWTException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class CustomControllerAdvice {

    //APIRefreshController에서 에러가 발생하면 CustomJWTException을 반환한다
    @ExceptionHandler(CustomJWTException.class)
    public ResponseEntity<?> handleJWTException(CustomJWTException e){
        String msg = e.getMessage();
        return ResponseEntity.ok().body(Map.of("error", msg));
    }
}
