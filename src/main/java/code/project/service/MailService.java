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

    public void sendCode(String to, String code) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, false, "UTF-8");

            // 표시명 포함 From 설정 (한글 안전)
            helper.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME, "UTF-8"));
            helper.setTo(to);
            helper.setSubject("[열려있나요?] 계정찾기 인증코드");

            // 일반 텍스트 메일
            String body = "인증코드: " + code + "\n유효시간: 5분";
            helper.setText(body, false);

            mailSender.send(mime);


        } catch (MessagingException e) {
            throw new RuntimeException("MAIL_SEND_FAILED");
        } catch (Exception e) {
            throw new RuntimeException("MAIL_SEND_FAILED");
        }
    }
}
