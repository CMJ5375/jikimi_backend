package code.project.controller;

import code.project.service.PasswordResetService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/password")
@CrossOrigin(origins = "http://localhost:3000")
public class PasswordResetController {

    private final PasswordResetService service;

    // 1) 코드 발송
    @PostMapping("/send-code")
    public ResponseEntity<?> send(@RequestBody SendReq req) {
        service.sendCode(req.getUsername(), req.getEmail());
        // 존재하지 않아도 동일 응답(정보 노출 방지)
        return ResponseEntity.noContent().build();
    }

    // 2) 코드 검증
    @PostMapping("/verify-code")
    public Map<String, Object> verify(@RequestBody VerifyReq req) {
        boolean ok = service.verifyCode(req.getUsername(), req.getEmail(), req.getCode());
        return Map.of("verified", ok);
    }

    // 3) 코드 + 새 비밀번호로 변경
    @PostMapping("/reset")
    public ResponseEntity<?> reset(@RequestBody ResetReq req) {
        boolean ok = service.resetPassword(req.getUsername(), req.getEmail(), req.getCode(), req.getNewPassword());
        if (!ok) return ResponseEntity.badRequest().body(Map.of("error", "VERIFY_FAILED"));
        return ResponseEntity.ok(Map.of("reset", true));
    }

    @Data public static class SendReq { private String username; private String email; }
    @Data public static class VerifyReq { private String username; private String email; private String code; }
    @Data public static class ResetReq { private String username; private String email; private String code; private String newPassword; }
}