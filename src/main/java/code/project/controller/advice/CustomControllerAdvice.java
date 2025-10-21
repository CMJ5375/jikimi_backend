package code.project.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class CustomControllerAdvice {

    //목록에 없는 번호 조회시 서버쪽에선 500번 에러
    //NoSuchElementException발생한다
    //<?>어떤 자료형을 써야할 지 모를때
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> notExist(NoSuchElementException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("msg", e.getMessage()));
    }

    //페이징처리시 list로 호출할때 페이지 번호가 잘못되었을 때
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> notExist(MethodArgumentNotValidException e){
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(Map.of("msg",e.getMessage()));
    }

}
