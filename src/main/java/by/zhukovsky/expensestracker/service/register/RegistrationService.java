package by.zhukovsky.expensestracker.service.register;

import by.zhukovsky.expensestracker.dto.RegistrationRequest;
import by.zhukovsky.expensestracker.entity.ConfirmationToken;
import by.zhukovsky.expensestracker.entity.user.User;
import by.zhukovsky.expensestracker.entity.user.UserRole;
import by.zhukovsky.expensestracker.service.UserService;
import by.zhukovsky.expensestracker.utils.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegistrationService {
    private final UserService userService;
    private final ConfirmationTokenService tokenService;
    private final EmailSenderService emailService;

    public RegistrationService(UserService userService, ConfirmationTokenService tokenService, EmailSenderService emailService) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    public String registerUser(RegistrationRequest request) {
        boolean isValidEmail = EmailValidator.isValidEmail(request.email());
        if (!isValidEmail) {
            throw new IllegalArgumentException("Incorrect email-address: " + request.email());
        }
        User createdUser = userService.createUser(new User(request.email(),
                request.password(),
                UserRole.USER_ROLE));

        String token = tokenService.generateToken(createdUser);

        String link = "http://localhost:8080/api/v1/registration/confirm?token=" + token;

        emailService.send(
                request.email(),
                link);
        return "User has been registered. Please, check your email to confirm and activate your account";
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = tokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("Token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalArgumentException("Email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token has expired");
        }

        tokenService.confirmToken(confirmationToken);
        userService.enableUser(confirmationToken.getUser());

        return "Confirmed";
    }
}
