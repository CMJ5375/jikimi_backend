package code.project.util;

import code.project.util.CustomJWTException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
public class JWTUtil {

    private static final String SECRET_KEY = "1234567890123456789012345678901234567890";

    // 기본 ROLE_USER 설정용 상수
    private static final List<String> DEFAULT_ROLE = List.of("USER");

    // 토큰 생성
    public static String generateToken(Map<String, Object> valueMap, int minutes) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes("UTF-8"));

            // roleNames 누락 방지 및 형식 보정
            Object rolesObj = valueMap.get("roleNames");
            List<String> roleNames;
            if (rolesObj instanceof String) {
                // 단일 문자열로 들어온 경우
                roleNames = List.of((String) rolesObj);
            } else if (rolesObj instanceof Collection<?>) {
                roleNames = new ArrayList<>();
                for (Object r : (Collection<?>) rolesObj) {
                    roleNames.add(String.valueOf(r));
                }
            } else {
                // 아무 것도 없는 경우 USER 기본
                roleNames = new ArrayList<>(DEFAULT_ROLE);
            }
            valueMap.put("roleNames", roleNames);

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

            Map<String, Object> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // roleNames가 문자열로 올 수도 있으니 보정
            Object roleObj = claims.get("roleNames");
            if (roleObj instanceof String) {
                claims.put("roleNames", List.of((String) roleObj));
            } else if (roleObj == null) {
                claims.put("roleNames", DEFAULT_ROLE);
            }

            return claims;

        } catch (ExpiredJwtException e) {
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

    // 만료된 토큰에서 클레임 추출 (refresh 용)
    public static Map<String, Object> getClaimsFromExpiredToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes("UTF-8"));

            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (Exception e) {
            throw new CustomJWTException("InvalidToken", e);
        }
    }
}
