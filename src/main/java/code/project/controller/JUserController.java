package code.project.controller;

import code.project.dto.JUserDTO;
import code.project.dto.JUserModifyDTO;
import code.project.service.JUserService;
import code.project.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@Slf4j
@RequiredArgsConstructor
public class JUserController {

    private final JUserService jUserService;
    private static String objToStr(Object o) { return (o == null) ? null : String.valueOf(o); }
    /* 헬스/로그인 체크 */
    @GetMapping("/project/user/login")
    public ResponseEntity<String> checkLogin() {
        return ResponseEntity.ok("ok");
    }

    @PostMapping(
            value = "/project/user/login",
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE },
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> login(
            @RequestBody(required = false) Map<String, Object> body,
            @RequestParam(required = false) MultiValueMap<String, String> form
    ) {
        try {
            String username = null, password = null;

            if (body != null) {
                username = Objects.toString(body.get("username"), null);
                password = Objects.toString(body.get("password"), null);
            } else if (form != null) {
                username = form.getFirst("username");
                password = form.getFirst("password");
            }

            if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
                return ResponseEntity.badRequest().body(Map.of("error", "아이디/비밀번호를 입력하세요."));
            }

            // 인증
            JUserDTO dto = jUserService.authenticate(username.trim(), password);
            if (dto == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "아이디/비밀번호가 올바르지 않습니다."));
            }

            // 토큰 생성 (비밀번호는 claims에 없음)
            Map<String, Object> claims = dto.getClaims();
            String accessToken  = JWTUtil.generateToken(claims, 10);        // 10분
            String refreshToken = JWTUtil.generateToken(claims, 60 * 24);   // 1일

            Map<String, Object> res = new HashMap<>();
            res.put("username", dto.getUsername());
            res.put("name", dto.getName());
            res.put("address", dto.getAddress());
            res.put("age", dto.getAge());
            res.put("email", dto.getEmail());
            res.put("profileImage", dto.getProfileImage());
            res.put("accessToken", accessToken);
            res.put("refreshToken", refreshToken);

            return ResponseEntity.ok(res);
        } catch (Exception e) {
            Throwable x = e; while (x.getCause()!=null) x = x.getCause();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "서버 오류", "detail", x.getMessage()));
        }
    }



    // JUserController.java
    @PostMapping(
            value = "/project/register",
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE },
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> register(
            @RequestBody(required = false) Map<String, Object> body,             // JSON
            @RequestParam(required = false) MultiValueMap<String, String> form   // x-www-form-urlencoded
    ) {
        try {
            // 1) 입력값 합치기
            String username = null, password = null, name = null, email = null;
            if (body != null) {
                username = trim(Objects.toString(body.get("username"), null));
                password = trim(Objects.toString(body.get("password"), null));
                name     = trim(Objects.toString(body.get("name"), null));
                email    = trim(Objects.toString(body.get("email"), null));
            } else if (form != null) {
                username = trim(form.getFirst("username"));
                password = trim(form.getFirst("password"));
                name     = trim(form.getFirst("name"));
                email    = trim(form.getFirst("email"));
            }

            log.info("[REGISTER] in username={}, email={}, name={}", username, email, name);

            if (!StringUtils.hasText(username)) {
                return ResponseEntity.badRequest().body(Map.of("error", "아이디(username)는 필수입니다."));
            }
            if (!StringUtils.hasText(password)) {
                return ResponseEntity.badRequest().body(Map.of("error", "비밀번호(password)는 필수입니다."));
            }

            // 2) DTO 구성 (JUserDTO는 기본생성자 없으므로 생성자 사용)
            JUserDTO dto = new JUserDTO(
                    null,            // userId
                    username,
                    password,
                    (name == null || name.isBlank()) ? "사용자" : name,            // name NOT NULL 가드
                    null,            // address
                    null,            // age
                    (email == null || email.isBlank()) ? (username + "@local.local") : email, // email NOT NULL 가드
                    null,            // profileImage
                    Collections.singletonList("USER") // 기본 권한
            );

            // 3) 서비스 호출
            String result = jUserService.register(dto);

            if (result != null && result.contains("이미 존재")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", result));
            }

            return ResponseEntity.ok(Map.of(
                    "message", (result == null ? "회원가입 성공" : result),
                    "username", dto.getUsername()
            ));
        } catch (DataIntegrityViolationException dive) {
            String cause = rootCauseMessage(dive);
            log.warn("[REGISTER] 제약 위반: {}", cause, dive);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "error", "제약 위반(UNIQUE/NOT NULL 등)",
                    "detail", cause
            ));
        } catch (Exception e) {
            String cause = rootCauseMessage(e);
            log.error("[REGISTER] 서버 예외: {}", cause, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "서버 오류",
                    "detail", cause
            ));
        }
    }

    private static String rootCauseMessage(Throwable t) {
        Throwable x = t;
        while (x.getCause() != null) x = x.getCause();
        String cls = x.getClass().getSimpleName();
        String msg = (x.getMessage() == null ? "" : x.getMessage());
        return cls + (msg.isBlank() ? "" : (": " + msg));
    }




    /* 회원정보 수정 */
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

    /* 프로필 업로드 (이미지 포함) */
    @PatchMapping(value = "/project/user/profile/{username}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateProfile(
            @PathVariable String username,
            @RequestPart(required = false) String name,
            @RequestPart(required = false) String address,
            @RequestPart(required = false) Integer age,
            @RequestPart(required = false) MultipartFile image) {

        var dto = jUserService.updateProfile(username, name, address, age, image);

        Map<String,Object> claims = dto.getClaims();
        String accessToken  = JWTUtil.generateToken(claims, 10);        // 10분
        String refreshToken = JWTUtil.generateToken(claims, 60 * 24);   // 1일

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

    /* ===== 헬퍼 ===== */

    private static JUserDTO coalesceDto(JUserDTO bodyDto, MultiValueMap<String, String> form) {
        if (bodyDto != null) {
            // 트림 & 롤 기본값 보정
            bodyDto.setUsername(trim(bodyDto.getUsername()));
            bodyDto.setPassword(trim(bodyDto.getPassword()));
            bodyDto.setName(trim(bodyDto.getName()));
            bodyDto.setEmail(trim(bodyDto.getEmail()));
            if (bodyDto.getRoleNames() == null || bodyDto.getRoleNames().isEmpty()) {
                bodyDto.setRoleNames(java.util.List.of("USER"));
            }
            return bodyDto;
        }

        if (form != null) {
            final String username = trim(form.getFirst("username"));
            final String password = trim(form.getFirst("password"));
            final String name     = trim(form.getFirst("name"));
            final String email    = trim(form.getFirst("email"));
            final String address  = trim(form.getFirst("address"));
            // age 같은 건 폼에 없을 수 있으니 null
            final Integer age     = null;

            // ⚠️ JUserDTO는 기본생성자 없음 → 파라미터 생성자 사용
            return new JUserDTO(
                    username,
                    password,
                    // 엔티티 제약(@Column(nullable=false)) 방어: null 대신 빈문자 허용
                    (name == null ? "" : name),
                    (address == null ? "" : address),
                    age,
                    (email == null ? "" : email),
                    null,                            // profileImage
                    java.util.List.of("USER")        // 기본 권한
            );
        }

        // body/form 둘 다 없으면 컨트롤러에서 400 처리
        return null;
    }

    private static String trim(String s) {
        return (s == null) ? null : s.trim();
    }

    private static ResponseEntity<Map<String, String>> bad(String msg) {
        return ResponseEntity.badRequest().body(Map.of("error", msg));
    }

    private static ResponseEntity<Map<String, String>> conflict(String msg) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", msg));
    }

    @Autowired(required = false)
    private org.springframework.jdbc.core.JdbcTemplate jdbc;

    @GetMapping(value="/project/diag/db", produces=MediaType.TEXT_PLAIN_VALUE)
    public String diagDb() {
        StringBuilder sb = new StringBuilder("DBCHECK:");
        try {
            if (jdbc == null) {
                return "DBCHECK:jdbc=null";
            }
            String ver = jdbc.queryForObject("select version()", String.class);
            String cur = jdbc.queryForObject("select current_user()", String.class);
            String db  = jdbc.queryForObject("select database()", String.class);
            Integer one = jdbc.queryForObject("select 1", Integer.class);
            sb.append(" ok")
                    .append(" | version=").append(ver)
                    .append(" | user=").append(cur)
                    .append(" | database=").append(db)
                    .append(" | select1=").append(one);
            return sb.toString();
        } catch (Exception e) {
            Throwable x=e; while(x.getCause()!=null) x=x.getCause();
            return "DBCHECK:ERROR " + x.getClass().getSimpleName() + " - " + x.getMessage();
        }
    }


}
