package code.project.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 인증코드 저장소
 *
 * - put(key, code, ttlSeconds): 코드 저장
 * - check(key, code): 유효성만 확인(소모 없음)
 * - verify(key, code): 유효성 확인 + 1회용 소진(성공 시 삭제)
 * - invalidate(key): 강제 무효화
 *
 * 권장: key에 namespace를 붙여 시나리오 간 충돌 방지
 *   예) 비밀번호 찾기: "PWD|" + email,  아이디 찾기: "ACC|" + email
 */
@Component
public class VerificationCodeStore {



    private static final class Entry {
        final String code;
        final Instant expireAt;
        Entry(String code, Instant expireAt) {
            this.code = code;
            this.expireAt = expireAt;
        }
    }

    /** key -> 코드 엔트리 (동시성 안전) */
    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    /** 코드 저장 (TTL 단위: 초) */
    public void put(String key, String code, long ttlSeconds) {
        store.put(key, new Entry(code, Instant.now().plusSeconds(ttlSeconds)));
    }

    /** 만료됐는지 판정(만료면 삭제) */
    private boolean isExpired(String key, Entry e) {
        if (e == null) return true;
        if (Instant.now().isAfter(e.expireAt)) {
            store.remove(key);
            return true;
        }
        return false;
    }

    /**
     * ✅ 검증(조회)만 수행 — 코드 소모 없음
     * @return 코드가 일치하고 만료되지 않았으면 true
     */
    public boolean check(String key, String code) {
        Entry e = store.get(key);
        if (isExpired(key, e)) return false;
        return e.code.equals(code);
    }

    /**
     * ✅ 검증 + 1회용 소진 — 성공 시 삭제
     * @return 코드가 일치하고 만료되지 않았으면 true (이때 삭제)
     */
    public boolean verify(String key, String code) {
        Entry e = store.get(key);
        if (isExpired(key, e)) return false;
        boolean ok = e.code.equals(code);
        if (ok) store.remove(key); // 1회용 소모
        return ok;
    }

    /** 강제 무효화(선택) */
    public void invalidate(String key) {
        store.remove(key);
    }
}
