package code.project.controller;

import code.project.domain.JUser;
import code.project.dto.JUserDTO;
import code.project.service.JUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/project")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") //React(CORS) 허용
public class JUserController {

    private final JUserService jUserService;


    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody JUserDTO dto) {
        String result = jUserService.register(dto);

        // 이미 존재하는 회원이라면
        if(result.contains("이미 존재")) {
            return ResponseEntity.badRequest().body(result);
        }

        // 정상 가입 시
        return ResponseEntity.ok(result);
    }
}
