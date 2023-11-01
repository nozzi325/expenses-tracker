package by.zhukovsky.expensestracker.service.auth;

import by.zhukovsky.expensestracker.entity.user.User;
import by.zhukovsky.expensestracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUserDetailsServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AuthUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_WithValidUser_ShouldReturnUserDetails() {
        String username = "test@example.com";
        User user = new User("John", "McClane", username, "password");
        user.setEnabled(true);
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_WithNonExistentUser_ShouldThrowUsernameNotFoundException() {
        String username = "nonexistent@example.com";
        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
    }

    @Test
    void loadUserByUsername_WithDisabledUser_ShouldThrowDisabledException() {
        String username = "disabled@example.com";
        User user = new User("John", "McClane", username, "password");
        user.setEnabled(false);
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));

        assertThrows(DisabledException.class, () -> userDetailsService.loadUserByUsername(username));
    }
}