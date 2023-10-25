package by.zhukovsky.expensestracker.service.register;

import by.zhukovsky.expensestracker.dto.request.RegistrationRequest;
import by.zhukovsky.expensestracker.dto.response.RegistrationResponse;
import by.zhukovsky.expensestracker.entity.ConfirmationToken;
import by.zhukovsky.expensestracker.entity.user.User;
import by.zhukovsky.expensestracker.service.UserService;
import by.zhukovsky.expensestracker.utils.EmailValidator;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegistrationService {
    private static final String BASE_URL = "http://localhost:8080/api/v1/registration/confirm?token=";

    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailSenderService emailService;

    public RegistrationService(UserService userService,
                               ConfirmationTokenService confirmationTokenService,
                               EmailSenderService emailService) {
        this.userService = userService;
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
    }

    public RegistrationResponse registerUser(RegistrationRequest request) {
        boolean isValidEmail = EmailValidator.isValidEmail(request.email());

        if (!isValidEmail) {
            return new RegistrationResponse("Invalid email address: " + request.email(), false);
        }

        User createdUser = userService.createUser(new User(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password()
        ));

        String token = confirmationTokenService.generateToken(createdUser);

        String link = BASE_URL + token;

        emailService.send(request.email(), link);

        return new RegistrationResponse("User registered. Please check your email for confirmation and account activation", true);
    }

    @Transactional
    public RegistrationResponse confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() ->
                        new EntityNotFoundException("Token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            return new RegistrationResponse("Email already confirmed", false);
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (expiredAt.isBefore(LocalDateTime.now())) {
            return new RegistrationResponse("Token has expired." +
                    "You can request a new confirmation token by clicking the 'Resend Confirmation Email' button.",
                    true);
        }

        confirmationTokenService.confirmToken(confirmationToken);
        userService.enableUser(confirmationToken.getUser());

        return new RegistrationResponse("Confirmed", true);
    }

    public RegistrationResponse regenerateTokenForUser(String email) {
        User user = userService.getUserByEmail(email);

        String newToken = confirmationTokenService.generateToken(user);

        String link = BASE_URL + newToken;

        emailService.send(email, link);

        return new RegistrationResponse("A new confirmation link has been sent to your email", true);
    }
}
