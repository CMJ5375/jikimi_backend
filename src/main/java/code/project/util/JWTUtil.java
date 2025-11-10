package code.project.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class JWTUtil {

    // ✅ 환경변수 JWT_SECRET 우선, 없으면 기존 상수값 사용 (호환성 유지)
    //    HS256 권장 최소 256비트(32바이트) 이상
    private static final String DEFAULT_SECRET = "1234567890123456789012345678901234567890";
    private static final String SECRET_SOURCE = Optional.ofNullable(System.getenv("JWT_SECRET"))
            .filter(s -> !s.isBlank())
            .orElse(DEFAULT_SECRET);

    // 기본 ROLE_USER 설정
    private static final List<String> DEFAULT_ROLE = List.of("USER");

    private static SecretKey getKey() {
        return Keys.hmacShaKeyFor(SECRET_SOURCE.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 토큰 생성
     * - valueMap 내 roleNames를 안전하게 정규화(List<String>)
     * - 누락/빈값이면 기본 USER 부여
     * - 발급/만료 시간 세팅
     */
    public static String generateToken(Map<String, Object> valueMap, int minutes) {
        Objects.requireNonNull(valueMap, "valueMap must not be null");

        // roleNames 정규화
        List<String> roleNames = normalizeRoleNames(valueMap.get("roleNames"));
        if (roleNames.isEmpty()) roleNames = new ArrayList<>(DEFAULT_ROLE);
        valueMap.put("roleNames", roleNames);

        Date iat = Date.from(ZonedDateTime.now().toInstant());
        Date exp = Date.from(ZonedDateTime.now().plusMinutes(minutes).toInstant());

        return Jwts.builder()
                .setHeader(Map.of("typ", "JWT"))
                .setClaims(valueMap)   // sub/username 등은 호출측에서 넣던 그대로 유지
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 검증 및 클레임 반환
     * - 검증 실패시 CustomJWTException으로 래핑 (기존 예외 계약 유지)
     * - roleNames를 항상 List<String> 형태로 보정하여 반환
     */
    public static Map<String, Object> validateToken(String token) {
        try {
            Claims body = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Map<String, Object> claims = new HashMap<>(body);

            // roleNames 보정 (String/누락 모두 케어)
            List<String> roleNames = normalizeRoleNames(claims.get("roleNames"));
            if (roleNames.isEmpty()) roleNames = new ArrayList<>(DEFAULT_ROLE);
            claims.put("roleNames", roleNames);

            return claims;

        } catch (ExpiredJwtException e) {
            log.warn("토큰 만료됨 (exp: {})", e.getClaims().getExpiration());
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

    /**
     * 만료된 토큰에서 클레임 추출 (refresh 용)
     * - 만료 예외일 때도 claims 반환
     */
    public static Map<String, Object> getClaimsFromExpiredToken(String token) {
        try {
            Claims body = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return new HashMap<>(body);
        } catch (ExpiredJwtException e) {
            // 만료된 경우에도 claims 사용
            return new HashMap<>(e.getClaims());
        } catch (Exception e) {
            throw new CustomJWTException("InvalidToken", e);
        }
    }

    // ===== 내부 유틸 =====

    /**
     * roleNames 입력을 List<String>으로 정규화
     * - List<?> → 각 요소를 문자열화
     * - String → CSV 분리
     * - null/기타 → 빈 리스트
     * - null/blank 제거, trim, 중복 제거, 원소는 그대로(대소문자/ROLE_ 접두어 보정은 필터에서)
     */
    @SuppressWarnings("unchecked")
    private static List<String> normalizeRoleNames(Object src) {
        if (src == null) return Collections.emptyList();

        if (src instanceof List<?>) {
            return ((List<?>) src).stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());
        }

        if (src instanceof String s) {
            if (s.isBlank()) return Collections.emptyList();
            return Arrays.stream(s.split(","))
                    .map(String::trim)
                    .filter(t -> !t.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());
        }

        // 그 외 타입은 문자열화 시도
        String s = String.valueOf(src).trim();
        if (s.isEmpty()) return Collections.emptyList();
        return List.of(s);
    }
}
