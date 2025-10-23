package code.project.controller;

import code.project.util.CustomJWTException;
import code.project.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class APIRefreshController {

    @RequestMapping("/project/user/refresh")
    public Map<String, Object> refresh(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("refreshToken") String refreshToken
    )
    {
        //refreshToken이 없음
        if(refreshToken == null) {
            throw new CustomJWTException("NULL_REFRASH");
        }

        //authHeader가 없거나 Bearer 이렇게 오는게 7자가 안되면 이상한 것임
        if(authHeader == null || authHeader.length() < 7) {
            throw new CustomJWTException("INVALID_STRING");  //잘못된 문자
        }

        String accessToken = authHeader.substring(7);

        //Access 토큰이 만료되지 않았다면, 그대로 사용
        if(checkExpiredToken(accessToken) == false ) {
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        //Refresh토큰 검증
        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);
        log.info("refresh ... claims: {}", claims);

        //새로운 accessToken발행
        String newAccessToken = JWTUtil.generateToken(claims, 10);

        return Map.of("accessToken", newAccessToken, "refreshToken", refreshToken);
    }

    private boolean checkTime(Integer exp) {
        //JWT (JSON Web Token)에서 가져온 exp 클레임은 Unix 타임스탬프로 표현됨
        //Unix 타임스탬프는 초단위로 시간을 나타냄
        //자바의 Data는 System.currentTimeMillis()는 밀리초 단위로 시간을 다룬다
        Date expDate = new Date( (long)exp * (1000));  //밀리세컨드로

        //현재 시간과의 차이 계산 - 밀리세컨즈
        long gap   = expDate.getTime() - System.currentTimeMillis();

        //분단위 계산
        long leftMin = gap / (1000 * 60);

        //1시간도 안남았는지..
        return leftMin < 60;
    }

    private boolean checkExpiredToken(String token) {
        try{
            JWTUtil.validateToken(token);  //검사
        }catch(CustomJWTException ex) {
            if(ex.getMessage().equals("Expired")){
                return true;
            }
        }
        return false;
    }
}
