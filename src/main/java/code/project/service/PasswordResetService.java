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

    /** username + email이 같은 행이 존재하는지 */
    @Transactional(readOnly = true)
    public boolean existsUser(String username, String email) {
        Long cnt = em.createQuery(
                        "select count(u) from JUser u where u.username = :username and u.email = :email", Long.class)
                .setParameter("username", username)
                .setParameter("email", email)
                .getSingleResult();
        return cnt != null && cnt > 0;
    }

    /** 1) 코드 발송: username + email 매칭 확인 후 전송 */
    @Transactional(readOnly = true)
    public boolean sendCode(String username, String email) {
        if (!existsUser(username, email)) {
            return false; // 컨트롤러에서 404 등으로 변환
        }
        String code = String.format("%06d", RND.nextInt(1_000_000));
        // 기존 키 정책 유지 (email 그대로 사용). 원하면 "PWD|" + email 로 네임스페이스화 가능.
        codeStore.put(email, code, 300); // 5분 유효
        mailService.sendCode(email, code);
        return true;
    }

    /** 2) 코드 검증(조회만, 소모 안 함) */
    public boolean verifyCode(String username, String email, String code) {
        return codeStore.check(email, code);
    }

    /** 3) 비밀번호 변경: 여기서 소모(1회용) + 해당 행 업데이트 */
    @Transactional
    public boolean resetPassword(String username, String email, String code, String newPassword) {
        boolean ok = codeStore.verify(email, code); // 일치하면 소모
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
