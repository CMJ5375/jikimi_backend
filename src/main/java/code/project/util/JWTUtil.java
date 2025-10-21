package code.project.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Slf4j
public class JWTUtil {
    private static String key = "1234567890123456789012345678901234567890";

    public static String generateToken(Map<String,Object> valueMap, int min) {
        SecretKey key = null;

        try {
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        String jwtStr = Jwts.builder()
                .setHeader(Map.of("typ", "JWT"))
                .setClaims(valueMap)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
                .signWith(key)
                .compact();

        return jwtStr;
    }

    //검증을 위한 validateToken()
    // 입력값 token은 검증대상의 JWT문자열
    public static Map<String, Object> validateToken(String token){
        //토큰의 클레임 데이터를 저장할 변수
        Map<String, Object> claim = null;
        //비밀키
        SecretKey key = null;
        try {
            //JWT 생성 시 사용한 키와 동일해야 함
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));

            claim = Jwts.parserBuilder()   // JWT 문자열을 파싱하는 객체를 빌드
                    .setSigningKey(key) //JWT의 서명을 검증하기 위해 사용할 비밀 키를 설정
                    .build()
                    .parseClaimsJws(token) //입력받은 JWT 문자열을 파싱하여 유효성을 확인, 서명이 유효한지, 토큰이 만료되지 않았는지 확인
                    .getBody();  //검증이 성공하면 토큰의 페이로드(Payload) 부분에 포함된 클레임 데이터를 반환
        } catch(MalformedJwtException malformedJwtException){
            throw new CustomJWTException("MalFormed"); //토큰이 잘못된 형식으로 작성된 경우
        }catch(ExpiredJwtException expiredJwtException){
            throw new CustomJWTException("Expired");  //토큰이 만료되었거나, 만료 시간이 잘못된 경우
        }catch(InvalidClaimException invalidClaimException){
            throw new CustomJWTException("Invalid");  //JWT 처리 중, 클레임 값이 특정 검증 조건을 충족하지 않을 때
        }catch(JwtException jwtException){
            throw new CustomJWTException("JWTError"); //JWT 생성, 검증, 또는 파싱 과정에서 발생할 수 있는 다양한 문제
        }catch(Exception e){
            throw new CustomJWTException("Error");
        }
        return claim;   //클레임 데이터는 Map<String, Object>로 반환
    }

}
