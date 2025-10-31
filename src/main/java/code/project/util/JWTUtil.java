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

    private static final String SECRET_KEY = "1234567890123456789012345678901234567890";

    // ✅ 토큰 생성
    public static String generateToken(Map<String, Object> valueMap, int minutes) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes("UTF-8"));

            return Jwts.builder()
                    .setHeader(Map.of("typ", "JWT"))
                    .setClaims(valueMap)
                    .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                    .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(minutes).toInstant()))
                    .signWith(key)
                    .compact();

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    // 토큰 검증 및 클레임 반환
    public static Map<String, Object> validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes("UTF-8"));

            // ⚡ 만료되지 않은 정상 토큰 → 정상 claims 반환
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            // ⚡ 만료된 토큰도 claims를 꺼낼 수 있음 → 재발급용으로 사용
            log.warn("토큰 만료됨 (만료 시간: {})", e.getClaims().getExpiration());
            throw new CustomJWTException("Expired", e);
        } catch (MalformedJwtException e) {
            throw new CustomJWTException("MalFormed", e);
        } catch (InvalidClaimException e) {
            throw new CustomJWTException("Invalid", e);
        } catch (JwtException e) {
            throw new CustomJWTException("JWTError", e);
        } catch (Exception e) {
            throw new CustomJWTException("Error", e);
        }
    }

    //  만료된 토큰에서 클레임 추출 (refresh 용)
    public static Map<String, Object> getClaimsFromExpiredToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes("UTF-8"));

            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // 만료된 경우에도 claims는 유효함
            return e.getClaims();
        } catch (Exception e) {
            throw new CustomJWTException("InvalidToken", e);
        }
    }
}
