package code.project.controller;

import code.project.domain.JUser;
import code.project.dto.JUserDTO;
import code.project.dto.JUserModifyDTO;
import code.project.service.JUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResponseEntity<JUserDTO> updateProfile(
            @PathVariable String username,
            @RequestPart(required = false) String name,
            @RequestPart(required = false) String address,
            @RequestPart(required = false) Integer age,
            @RequestPart(required = false) MultipartFile image
    ) {
        JUserDTO dto = jUserService.updateProfile(username, name, address, age, image);
        return ResponseEntity.ok(dto);
    }
}
