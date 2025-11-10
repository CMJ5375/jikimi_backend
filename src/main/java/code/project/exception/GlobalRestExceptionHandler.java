// code/project/exception/GlobalRestExceptionHandler.java
package code.project.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalRestExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "error", "제약 위반(UNIQUE/NOT NULL 등)",
                "detail", rootCauseMessage(ex)
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAny(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "서버 오류",
                "detail", rootCauseMessage(ex)
        ));
    }

    private static String rootCauseMessage(Throwable t) {
        Throwable x = t;
        while (x.getCause() != null) x = x.getCause();
        String cls = x.getClass().getSimpleName();
        String msg = (x.getMessage() == null ? "" : x.getMessage());
        return cls + (msg.isBlank() ? "" : (": " + msg));
    }
}
