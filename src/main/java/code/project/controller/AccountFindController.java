package code.project.controller;

import code.project.service.AccountFindService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
@CrossOrigin(origins = "http://localhost:3000")
public class AccountFindController {

    private final AccountFindService accountFindService;

    @PostMapping("/send-code")
    public ResponseEntity<Void> sendCode(@RequestBody EmailReq req) {
        accountFindService.sendCode(req.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verify-code")
    public Map<String, Object> verify(@RequestBody VerifyReq req) {
        boolean ok = accountFindService.verifyCode(req.getEmail(), req.getCode());
        return Map.of("verified", ok);
    }

    // 인증 성공 후: email -> username(아이디) 반환
    @GetMapping("/username")
    public ResponseEntity<?> getUsername(@RequestParam String email) {
        String username = accountFindService.getUsernameByEmail(email);
        if (username == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("username", username));
    }

    @Data static class EmailReq { private String email; }
    @Data static class VerifyReq { private String email; private String code; }
}
