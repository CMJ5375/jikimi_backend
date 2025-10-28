package code.project.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    private static final String FROM_EMAIL = "dodojun0913@gmail.com";
    private static final String FROM_NAME  = "(주) 열려있나요?";

    /** 제목을 '인증번호 123456' 형태로 전송 */
    public void sendCode(String to, String code) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");

            // 표시명 포함 From 설정 (한글 안전)
            helper.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME, "UTF-8"));
            helper.setTo(to);

            // 🔵 제목에 인증번호 직접 표기
            helper.setSubject("인증번호 " + code);   // 예: "인증번호 123456"
            // 만약 숫자만 제목으로 원하면: helper.setSubject(code);

            // 본문(텍스트)
            String body = "요청하신 인증번호는 아래와 같습니다.\n\n"
                    + code + "\n\n"
                    + "유효시간: 5분\n";
            helper.setText(body, false);

            mailSender.send(mime);
        } catch (MessagingException e) {
            log.error("메일 전송 실패(MessagingException): to={}, code={}, err={}", to, code, e.getMessage(), e);
            throw new RuntimeException("MAIL_SEND_FAILED", e);
        } catch (Exception e) {
            log.error("메일 전송 실패(Exception): to={}, code={}, err={}", to, code, e.getMessage(), e);
            throw new RuntimeException("MAIL_SEND_FAILED", e);
        }
    }
}
