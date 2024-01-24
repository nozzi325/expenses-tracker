package by.zhukovsky.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailSenderService.class);

    @Value("${from.address}")
    private String fromAddress;

    @Value("${email.subject}")
    private String emailSubject;

    private final JavaMailSender mailSender;

    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void send(String to, String confirmationLink) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            String emailBody = buildEmailBody(confirmationLink);

            helper.setText(emailBody, true);
            helper.setTo(to);
            helper.setSubject(emailSubject);
            helper.setFrom(fromAddress);

            mailSender.send(mimeMessage);

            LOGGER.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            LOGGER.error("Failed to send email to: {}", to, e);
            throw new IllegalStateException("Failed to send email", e);
        }
    }

    private String buildEmailBody(String confirmationLink) {
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
