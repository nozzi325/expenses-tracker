package by.zhukovsky.service;

import by.zhukovsky.dto.MailParams;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class ConsumerService {
    private final EmailSenderService emailSenderService;

    public ConsumerService(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @RabbitListener(queues = "${spring.rabbitmq.queues.registration-mail}")
    public void consumeRegistrationMail(MailParams mailParams) {
        emailSenderService.send(mailParams.emailTo(), mailParams.link());
    }
}
