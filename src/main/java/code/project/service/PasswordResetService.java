package code.project.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final VerificationCodeStore codeStore;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager em;

    private static final SecureRandom RND = new SecureRandom();

    /** username + email이 같은 사용자 존재 여부 */
    @Transactional(readOnly = true)
    public boolean existsUser(String username, String email) {
        Long cnt = em.createQuery(
                        "select count(u) from JUser u where u.username = :username and u.email = :email", Long.class)
                .setParameter("username", username)
                .setParameter("email", email)
                .getSingleResult();
        return cnt != null && cnt > 0;
    }

    private String key(String email) {
        // 계정찾기 코드와 충돌 방지용 prefix
        return "PWD|" + (email == null ? "" : email.trim().toLowerCase());
    }

    /** 1) 코드 발송: username + email 검증 후 전송 (정보노출 방지: 존재여부와 무관하게 동일 응답) */
    @Transactional(readOnly = true)
    public void sendCode(String username, String email) {
        // 존재하지 않는 조합이어도 같은 응답으로 처리(보안상)
        String code = String.format("%06d", RND.nextInt(1_000_000));
        codeStore.put(key(email), code, 300); // 5분 유효
        mailService.sendCode(email, code);
    }

    /** 2) 코드 검증: ✅ 삭제 없이 유효성만 확인 */
    public boolean verifyCode(String username, String email, String code) {
        return codeStore.check(key(email), code);
    }

    /** 3) 비밀번호 변경: ✅ 여기서 최종 소진(1회용) 후 업데이트 */
    @Transactional
    public boolean resetPassword(String username, String email, String code, String newPassword) {
        boolean ok = codeStore.verify(key(email), code); // 소진은 여기서!
        if (!ok) return false;

        String enc = passwordEncoder.encode(newPassword);
        int updated = em.createQuery(
                        "update JUser u set u.password = :pw where u.username = :username and u.email = :email")
                .setParameter("pw", enc)
                .setParameter("username", username)
                .setParameter("email", email)
                .executeUpdate();
        return updated > 0;
    }
}
