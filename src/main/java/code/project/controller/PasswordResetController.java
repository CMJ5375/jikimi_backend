package code.project.controller;

import code.project.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/password")
public class PasswordResetController {

    private final PasswordResetService svc;

    // 1) 발송
    @PostMapping("/send-code")
    public ResponseEntity<?> send(@RequestBody SendReq req) {
        boolean ok = svc.sendCode(req.username(), req.email());
        if (!ok) {
            return ResponseEntity.status(404).body(Map.of("error", "USER_EMAIL_MISMATCH"));
        }
        return ResponseEntity.noContent().build(); // 204
    }

    // 2) 검증
    @PostMapping("/verify-code")
    public ResponseEntity<?> verify(@RequestBody VerifyReq req) {
        boolean ok = svc.verifyCode(req.username(), req.email(), req.code());
        if (!ok) {
            return ResponseEntity.badRequest().body(Map.of("verified", false, "error", "VERIFY_FAILED"));
        }
        return ResponseEntity.ok(Map.of("verified", true));
    }

    // 3) 변경
    @PostMapping("/reset")
    public ResponseEntity<?> reset(@RequestBody ResetReq req) {
        boolean ok = svc.resetPassword(req.username(), req.email(), req.code(), req.newPassword());
        if (!ok) {
            return ResponseEntity.badRequest().body(Map.of("reset", false, "error", "VERIFY_FAILED"));
        }
        return ResponseEntity.ok(Map.of("reset", true));
    }

    public record SendReq(String username, String email) {}
    public record VerifyReq(String username, String email, String code) {}
    public record ResetReq(String username, String email, String code, String newPassword) {}
}
