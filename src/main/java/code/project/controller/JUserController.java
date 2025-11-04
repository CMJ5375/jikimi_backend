package code.project.controller;

import code.project.domain.JUser;
import code.project.dto.JUserDTO;
import code.project.dto.JUserModifyDTO;
import code.project.service.JUserService;
import code.project.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") //React(CORS) 허용
public class JUserController {

    private final JUserService jUserService;


    // 회원가입
    @PostMapping("/project/register")
    public ResponseEntity<String> register(@RequestBody JUserDTO dto) {

        log.info("회원가입 요청 수신 {}", dto);
        String result = jUserService.register(dto);

        // 이미 존재하는 회원이라면
        if(result.contains("이미 존재")) {
            return ResponseEntity.badRequest().body(result);
        }

        // 정상 가입 시
        return ResponseEntity.ok(result);
    }

    //회원정보 수정
    @PutMapping("/project/user/modify/{username}")
    public Map<String, String> modify(
            @RequestBody JUserModifyDTO jUserModifyDTO,
            @PathVariable("username") String username
    ) {
        jUserModifyDTO.setUsername(username);
        log.info("member modify: {}", jUserModifyDTO);
        jUserService.modifyUser(jUserModifyDTO);

        return Map.of("result", "modify");
    }

    //프로필 업로드
    @PatchMapping(value = "/project/user/profile/{username}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateProfile(
            @PathVariable String username,
            @RequestPart(required = false) String name,
            @RequestPart(required = false) String address,
            @RequestPart(required = false) Integer age,
            @RequestPart(required = false) MultipartFile image) {

        JUserDTO dto = jUserService.updateProfile(username, name, address, age, image);

        // ✅ 프로필 반영된 최신 클레임으로 토큰 재발급
        Map<String,Object> claims = dto.getClaims();
        String accessToken  = JWTUtil.generateToken(claims, 10);        // 10분 예시
        String refreshToken = JWTUtil.generateToken(claims, 60 * 24);   // 1일 예시

        // ✅ 프론트가 바로 쿠키/리덕스 갱신할 수 있도록 함께 반환
        Map<String, Object> body = new HashMap<>();
        body.put("username", dto.getUsername());
        body.put("name", dto.getName());
        body.put("address", dto.getAddress());
        body.put("age", dto.getAge());
        body.put("email", dto.getEmail());
        body.put("profileImage", dto.getProfileImage());
        body.put("accessToken", accessToken);
        body.put("refreshToken", refreshToken);

        return ResponseEntity.ok(body);
    }
}
