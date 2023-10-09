package by.zhukovsky.expensestracker.service;

import by.zhukovsky.expensestracker.entity.ConfirmationToken;
import by.zhukovsky.expensestracker.entity.user.User;
import by.zhukovsky.expensestracker.repository.ConfirmationTokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository repository;

    public ConfirmationTokenService(ConfirmationTokenRepository repository) {
        this.repository = repository;
    }

    public String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
        repository.save(confirmationToken);
        return token;
    }

    public Optional<ConfirmationToken> getToken(String token) {
        return repository.findByToken(token);
    }

    public void confirmToken(ConfirmationToken confirmationToken) {
        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("Token already confirmed");
        }
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        repository.save(confirmationToken);
    }
}
