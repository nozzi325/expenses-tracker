package by.zhukovsky.expensestracker.service.register;

import by.zhukovsky.expensestracker.entity.ConfirmationToken;
import by.zhukovsky.expensestracker.entity.user.User;
import by.zhukovsky.expensestracker.repository.ConfirmationTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ConfirmationTokenServiceTest {
    @Mock
    private ConfirmationTokenRepository repository;
    @InjectMocks
    private ConfirmationTokenService confirmationTokenService;

    @Test
    void generateToken_ShouldReturnValidTokenAndSaveToRepository() {
        User user = new User("John", "McClane", "john@example.com", "password");

        LocalDateTime now = LocalDateTime.now();
        when(repository.save(Mockito.any(ConfirmationToken.class))).thenAnswer(invocation -> {
            ConfirmationToken savedToken = invocation.getArgument(0);
            assertNotNull(savedToken.getToken());
            assertEquals(user, savedToken.getUser());
            assertNotNull(savedToken.getExpiresAt());
            assertNotNull(savedToken.getCreatedAt());
            assertNull(savedToken.getConfirmedAt());

            Duration duration = Duration.between(now, savedToken.getExpiresAt());
            assertEquals(15, duration.toMinutes());

            return savedToken;
        });

        String generatedToken = confirmationTokenService.generateToken(user);

        assertNotNull(generatedToken);
    }

    @Test
    void getToken_WithValidToken_ShouldReturnOptionalWithConfirmationToken() {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), new User());

        when(repository.findByToken(token)).thenReturn(Optional.of(confirmationToken));

        Optional<ConfirmationToken> result = confirmationTokenService.getToken(token);

        assertTrue(result.isPresent());
        assertEquals(confirmationToken, result.get());
    }

    @Test
    void getToken_WithInvalidToken_ShouldReturnEmptyOptional() {
        String token = UUID.randomUUID().toString();

        when(repository.findByToken(token)).thenReturn(Optional.empty());

        Optional<ConfirmationToken> result = confirmationTokenService.getToken(token);

        assertTrue(result.isEmpty());
    }

    @Test
    void confirmToken_WithUnconfirmedToken_ShouldSetConfirmedAtAndSaveToRepository() {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), new User());

        when(repository.save(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0));

        confirmationTokenService.confirmToken(confirmationToken);

        assertNotNull(confirmationToken.getConfirmedAt());
        verify(repository).save(confirmationToken);
    }

    @Test
    void confirmToken_WithAlreadyConfirmedToken_ShouldThrowIllegalStateException() {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), new User());
        confirmationToken.setConfirmedAt(LocalDateTime.now());

        assertThrows(IllegalStateException.class, () -> confirmationTokenService.confirmToken(confirmationToken));
    }
}