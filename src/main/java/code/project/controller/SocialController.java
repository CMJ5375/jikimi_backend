package code.project.controller;

import code.project.dto.JUserDTO;
import code.project.service.JUserService;
import code.project.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 소셜 로그인 (카카오) 컨트롤러
 * - 프론트에서 먼저 Kakao access_token 을 받아온 뒤,
 *   그 토큰을 /project/user/kakao?accessToken=... 으로 넘겨주는 구조.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/project/user")
public class SocialController {

    private final JUserService jUserService;

    /**
     * GET /project/user/kakao?accessToken=KAKAO_ACCESS_TOKEN
     *
     * 1) Kakao accessToken으로 카카오 유저 정보 조회 + 우리 서비스 유저 생성/조회
     * 2) 우리 서비스용 JWT accessToken / refreshToken 생성
     * 3) 일반 로그인과 비슷한 형태의 JSON 반환
     */
    @GetMapping("/kakao")
    public ResponseEntity<Map<String, Object>> kakaoLogin(
            @RequestParam("accessToken") String kakaoAccessToken) {

        log.info("[KAKAO] accessToken 기반 로그인 시도");

        // 1) Kakao accessToken → 우리 서비스 유저 조회/생성
        JUserDTO dto = jUserService.getKakaoUser(kakaoAccessToken);

        // 2) 우리 서비스 JWT 발급 (기존 /project/user/login 과 동일 패턴)
        Map<String, Object> claims = dto.getClaims();
        String jwtAccessToken  = JWTUtil.generateToken(claims, 10);        // 10분
        String jwtRefreshToken = JWTUtil.generateToken(claims, 60 * 24);   // 1일

        // 3) 응답 JSON 구성
        Map<String, Object> res = new HashMap<>();
        res.put("userId", dto.getUserId());
        res.put("username", dto.getUsername());
        res.put("name", dto.getName());
        res.put("address", dto.getAddress());
        res.put("age", dto.getAge());
        res.put("email", dto.getEmail());
        res.put("profileImage", dto.getProfileImage());
        res.put("roleNames", dto.getRoleNames());
        res.put("accessToken", jwtAccessToken);
        res.put("refreshToken", jwtRefreshToken);

        log.info("[KAKAO] 로그인 성공: username={}", dto.getUsername());

        return ResponseEntity.ok(res);
    }
}
