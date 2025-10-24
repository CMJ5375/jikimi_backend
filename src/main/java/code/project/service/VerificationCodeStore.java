package code.project.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 인증코드 저장소
 *
 * - put(key, code, ttlSeconds): 코드 저장
 * - check(key, code): 유효성만 확인(삭제 안 함)
 * - verify(key, code): 유효성 확인 + 1회용 소진(성공 시 삭제)
 * - invalidate(key): 강제 무효화(선택)
 *
 * key에는 namespace를 붙여 서로 다른 시나리오가 서로 간섭하지 않도록 권장합니다.
 *   예) 비밀번호 찾기: "PWD|" + email,  아이디 찾기: "ACC|" + email
 */
@Component
public class VerificationCodeStore {

    private static class Entry {
        final String code;
        final Instant expireAt;
        Entry(String code, Instant expireAt) {
            this.code = code;
            this.expireAt = expireAt;
        }
    }

    private final Map<String, Entry> store = new ConcurrentHashMap<>();

    /** 코드 저장 (TTL 단위: 초) */
    public void put(String key, String code, long ttlSeconds) {
        store.put(key, new Entry(code, Instant.now().plusSeconds(ttlSeconds)));
    }

    /** 유효성만 확인(삭제 없음) */
    public boolean check(String key, String code) {
        Entry e = store.get(key);
        if (e == null) return false;
        if (Instant.now().isAfter(e.expireAt)) {
            store.remove(key);
            return false;
        }
        return e.code.equals(code);
    }

    /** 유효성 확인 + 1회용 소진(성공 시 삭제) */
    public boolean verify(String key, String code) {
        Entry e = store.get(key);
        if (e == null) return false;
        if (Instant.now().isAfter(e.expireAt)) {
            store.remove(key);
            return false;
        }
        boolean ok = e.code.equals(code);
        if (ok) store.remove(key); // 1회용
        return ok;
    }

    /** 강제 무효화(선택 사용) */
    public void invalidate(String key) {
        store.remove(key);
    }
}
