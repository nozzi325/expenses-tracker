package by.zhukovsky.expensestracker.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailSenderService.class);
    private static final String FROM_ADDRESS = "andrew-app@test.com";
    private static final String SUBJECT = "Email confirmation";
    private final JavaMailSender mailSender;

    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void send(String to, String confirmationLink) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            String emailContent = getEmailContent(confirmationLink);
            helper.setText(emailContent, true);
            helper.setTo(to);
            helper.setSubject(SUBJECT);
            helper.setFrom(FROM_ADDRESS);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send email", e);
            throw new IllegalStateException("Failed to send email", e);
        }
    }

    private String getEmailContent(String confirmationLink) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Email Confirmation</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>Email Confirmation</h1>\n" +
                "    <p>Thank you for registering with our service. Please click the link below to confirm your email:</p>\n" +
                "    <p><a href=\"" + confirmationLink + "\">Confirm Email</a></p>\n" +
                "    <p>If you did not register on our website, you can ignore this email.</p>\n" +
                "</body>\n" +
                "</html>";
    }
}
