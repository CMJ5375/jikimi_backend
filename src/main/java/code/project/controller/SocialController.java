package code.project.controller;

import code.project.dto.KakaoUserInfoDTO;
import code.project.dto.UserDTO;
import code.project.service.UserService;
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

    private final UserService userService;

    @GetMapping("/project/user/kakao")
    public Map<String, Object> getUserFromKakao(@RequestParam("accessToken") String accessToken) {
        log.info("react에서 가져온 access Token {}", accessToken);

        UserDTO userDTO = userService.getKakaoUser(accessToken);

        Map<String, Object> claims = userDTO.getClaims();

        String jwtAccessToken = JWTUtil.generateToken(claims, 10);
        String jwtRefreshToken = JWTUtil.generateToken(claims, 60 * 24);

        claims.put("accessToken", jwtAccessToken);
        claims.put("refreshToken", jwtRefreshToken);

        return claims;
    }
}
