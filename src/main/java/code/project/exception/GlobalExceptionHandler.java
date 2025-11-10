// src/main/java/code/project/exception/GlobalExceptionHandler.java
package code.project.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> all(Exception ex) {
        String msg = ex.getMessage();
        if (ex.getCause()!=null && ex.getCause().getMessage()!=null) {
            msg += " | cause: " + ex.getCause().getMessage();
        }
        return ResponseEntity.status(500).body(Map.of("message", msg==null?"Internal Server Error":msg));
    }
}

