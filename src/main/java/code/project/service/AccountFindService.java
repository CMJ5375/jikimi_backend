package code.project.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AccountFindService {

    private final VerificationCodeStore codeStore;
    private final MailService mailService;

    @PersistenceContext
    private EntityManager em;

    private static final SecureRandom RND = new SecureRandom();

    /** 1) 코드 발송 */
    public void sendCode(String email) {
        String code = String.format("%06d", RND.nextInt(1_000_000));
        codeStore.put(email, code, 300); // 5분 유효
        mailService.sendCode(email, code);
    }

    /** 2) 코드 검증 */
    public boolean verifyCode(String email, String code) {
        return codeStore.verify(email, code);
    }

    /** 3) email -> username(아이디) */
    @Transactional(readOnly = true)
    public String getUsernameByEmail(String email) {
        return em.createQuery(
                        "select u.username from JUser u where u.email = :email", String.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
