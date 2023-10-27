package by.zhukovsky.expensestracker.service.register;

import by.zhukovsky.expensestracker.dto.request.RegistrationRequest;
import by.zhukovsky.expensestracker.dto.response.RegistrationResponse;
import by.zhukovsky.expensestracker.entity.ConfirmationToken;
import by.zhukovsky.expensestracker.entity.user.User;
import by.zhukovsky.expensestracker.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RegistrationServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @Mock
    private EmailSenderService emailService;
    @InjectMocks
    private RegistrationService registrationService;


    @Test
    void registerUser_WithValidRequest_ShouldReturnSuccessfulResponse() {
        RegistrationRequest request = new RegistrationRequest(
                "john@example.com", "password", "John", "McClane");
        User user = new User(request.firstName(), request.lastName(), request.email(), request.password());

        when(userService.createUser(user)).thenReturn(user);
        when(confirmationTokenService.generateToken(user)).thenReturn("mockToken");

        RegistrationResponse response = registrationService.registerUser(request);

        verify(emailService).send(anyString(), anyString());
        assertNotNull(response);
        assertTrue(response.success());
        assertEquals("User registered. Please check your email for confirmation and account activation",
                response.message());
    }

    @Test
    void registerUser_WithInvalidEmail_ShouldReturnErrorResponse() {
        RegistrationRequest request = new RegistrationRequest(
                "invalid-email", "password", "John", "McClane");

        RegistrationResponse response = registrationService.registerUser(request);

        assertNotNull(response);
        assertFalse(response.success());
        assertTrue(response.message().contains("Invalid email address"));
    }

    @Test
    void confirmToken_WithValidToken_ShouldReturnSuccessfulResponse() {
        String token = "validToken";
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setConfirmedAt(null);
        confirmationToken.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(confirmationTokenService.getToken(token)).thenReturn(Optional.of(confirmationToken));

        RegistrationResponse response = registrationService.confirmToken(token);

        verify(userService).enableUser(confirmationToken.getUser());
        assertNotNull(response);
        assertTrue(response.success());
        assertEquals("Confirmed", response.message());
    }

    @Test
    void confirmToken_WithExpiredToken_ShouldReturnErrorResponse() {
        String token = "expiredToken";
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setConfirmedAt(null);
        confirmationToken.setExpiresAt(LocalDateTime.now().minusDays(1));

        when(confirmationTokenService.getToken(token)).thenReturn(Optional.of(confirmationToken));

        RegistrationResponse response = registrationService.confirmToken(token);

        verify(userService, never()).enableUser(confirmationToken.getUser());
        assertNotNull(response);
        assertFalse(response.success());
        assertTrue(response.message().contains("Token has expired"));
    }

    @Test
    void confirmToken_WithAlreadyConfirmedToken_ShouldReturnErrorResponse() {
        String token = "confirmedToken";
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationToken.setExpiresAt(LocalDateTime.now().plusDays(1));

        when(confirmationTokenService.getToken(token)).thenReturn(Optional.of(confirmationToken));

        RegistrationResponse response = registrationService.confirmToken(token);

        verify(userService, never()).enableUser(confirmationToken.getUser());
        assertNotNull(response);
        assertFalse(response.success());
        assertTrue(response.message().contains("Email already confirmed"));
    }

    @Test
    void regenerateTokenForUser_ShouldReturnSuccessfulResponse() {
        String email = "john@example.com";
        User user = new User("John", "McClane", email, "password");

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(confirmationTokenService.generateToken(user)).thenReturn("newToken");

        RegistrationResponse response = registrationService.regenerateTokenForUser(email);

        verify(emailService).send(anyString(), anyString());
        assertNotNull(response);
        assertTrue(response.success());
        assertEquals("A new confirmation link has been sent to your email", response.message());
    }

    @Test
    void confirmToken_WithNonExistentToken_ShouldThrowEntityNotFoundException() {
        String token = "nonExistentToken";

        when(confirmationTokenService.getToken(token)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> registrationService.confirmToken(token));
    }
}