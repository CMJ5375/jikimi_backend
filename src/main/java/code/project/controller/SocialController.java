package code.project.controller;

import code.project.dto.JUserDTO;
import code.project.service.JUserService;
import code.project.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SocialController {

    private final JUserService JUserService;

    @GetMapping("/project/user/kakao")
    public Map<String, Object> getUserFromKakao(@RequestParam("accessToken") String accessToken) {
        log.info("react에서 가져온 access Token {}", accessToken);

        JUserDTO JUserDTO = JUserService.getKakaoUser(accessToken);

        Map<String, Object> claims = JUserDTO.getClaims();

        String jwtAccessToken = JWTUtil.generateToken(claims, 10);
        String jwtRefreshToken = JWTUtil.generateToken(claims, 60 * 24);

        claims.put("accessToken", jwtAccessToken);
        claims.put("refreshToken", jwtRefreshToken);

        return claims;
    }
}
