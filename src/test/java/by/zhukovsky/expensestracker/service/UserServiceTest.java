package by.zhukovsky.expensestracker.service;

import by.zhukovsky.expensestracker.dto.request.UserUpdateRequest;
import by.zhukovsky.expensestracker.entity.user.User;
import by.zhukovsky.expensestracker.exception.InvalidRequestException;
import by.zhukovsky.expensestracker.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    void createUser_WithNonExistingEmail_ShouldCreateUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userRepository.existsByEmailEqualsIgnoreCase(user.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(user)).thenReturn(user);

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser);
        assertEquals("encodedPassword", createdUser.getPassword());
        assertEquals("test@example.com", createdUser.getEmail());
    }

    @Test
    void createUser_WithExistingEmail_ShouldThrowEntityExistsException() {
        User user = new User();
        user.setEmail("test@example.com");

        when(userRepository.existsByEmailEqualsIgnoreCase(user.getEmail())).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> userService.createUser(user));
    }

    @Test
    void getUserById_WithExistingUserId_ShouldReturnUser() {
        Long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserById(userId);

        assertEquals(user, result);
    }

    @Test
    void getUserById_WithNonExistingUserId_ShouldThrowEntityNotFoundException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void getUserByEmail_WithExistingEmail_ShouldReturnUser() {
        String email = "test@example.com";
        User user = new User();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.getUserByEmail(email);

        assertEquals(user, result);
    }

    @Test
    void getUserByEmail_WithNonExistingEmail_ShouldThrowEntityNotFoundException() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserByEmail(email));
    }

    @Test
    void updateUser_WithAllFieldsChanged_ShouldUpdateUser() {
        Long userId = 1L;
        User originalUser = new User();
        originalUser.setFirstName("OriginalFirstName");
        originalUser.setLastName("OriginalLastName");
        originalUser.setEmail("original@example.com");
        originalUser.setPassword("originalPassword");
        UserUpdateRequest updateRequest = new UserUpdateRequest(
                "NewFirstName",
                "NewLastName",
                "new@example.com",
                "newPassword"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(originalUser));
        when(passwordEncoder.matches(updateRequest.password(), originalUser.getPassword())).thenReturn(true);
        when(userRepository.existsByEmailEqualsIgnoreCase(updateRequest.email())).thenReturn(false);
        when(userRepository.save(originalUser)).thenReturn(originalUser);

        User updatedUser = userService.updateUser(userId, updateRequest);

        assertEquals("NewFirstName", updatedUser.getFirstName());
        assertEquals("NewLastName", updatedUser.getLastName());
        assertEquals("new@example.com", updatedUser.getEmail());
        assertTrue(passwordEncoder.matches("newPassword", updatedUser.getPassword()));
    }

    @Test
    void updateUser_WithNonExistingUser_ShouldThrowEntityNotFoundException() {
        Long userId = 1L;
        UserUpdateRequest updateRequest = new UserUpdateRequest(
                "NewFirstName",
                "NewLastName",
                "new@example.com",
                "newPassword"
        );

        when(userRepository.findById(userId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(userId, updateRequest));
    }

    @Test
    void updateUser_WithFirstNameChanged_ShouldUpdateUser() {
        Long userId = 1L;
        User originalUser = new User();
        originalUser.setFirstName("OriginalFirstName");
        originalUser.setLastName("OriginalLastName");
        originalUser.setEmail("original@example.com");
        originalUser.setPassword("originalPassword");

        UserUpdateRequest updateRequest = new UserUpdateRequest(
                "NewFirstName",
                "OriginalLastName",
                "original@example.com",
                "originalPassword"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(originalUser));
        when(passwordEncoder.matches(updateRequest.password(), originalUser.getPassword())).thenReturn(true);
        when(userRepository.save(originalUser)).thenReturn(originalUser);

        User updatedUser = userService.updateUser(userId, updateRequest);

        assertEquals("NewFirstName", updatedUser.getFirstName());
        assertEquals("OriginalLastName", updatedUser.getLastName());
        assertEquals("original@example.com", updatedUser.getEmail());
        assertEquals("originalPassword", updatedUser.getPassword());
    }

    @Test
    void updateUser_WithLastNameChanged_ShouldUpdateUser() {
        Long userId = 1L;
        User originalUser = new User();
        originalUser.setFirstName("OriginalFirstName");
        originalUser.setLastName("OriginalLastName");
        originalUser.setEmail("original@example.com");
        originalUser.setPassword("originalPassword");

        UserUpdateRequest updateRequest = new UserUpdateRequest(
                "OriginalFirstName",
                "NewLastName",
                "original@example.com",
                "originalPassword"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(originalUser));
        when(passwordEncoder.matches(updateRequest.password(), originalUser.getPassword())).thenReturn(true);
        when(userRepository.save(originalUser)).thenReturn(originalUser);

        User updatedUser = userService.updateUser(userId, updateRequest);

        assertEquals("OriginalFirstName", updatedUser.getFirstName());
        assertEquals("NewLastName", updatedUser.getLastName());
        assertEquals("original@example.com", updatedUser.getEmail());
        assertEquals("originalPassword", updatedUser.getPassword());
    }

    @Test
    void updateUser_WithEmailChanged_ShouldUpdateUser() {
        Long userId = 1L;
        User originalUser = new User();
        originalUser.setFirstName("OriginalFirstName");
        originalUser.setLastName("OriginalLastName");
        originalUser.setEmail("original@example.com");
        originalUser.setPassword("originalPassword");

        UserUpdateRequest updateRequest = new UserUpdateRequest(
                "OriginalFirstName",
                "OriginalLastName",
                "new@example.com",
                "originalPassword"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(originalUser));
        when(passwordEncoder.matches(updateRequest.password(), originalUser.getPassword())).thenReturn(true);
        when(userRepository.existsByEmailEqualsIgnoreCase(updateRequest.email())).thenReturn(false);
        when(userRepository.save(originalUser)).thenReturn(originalUser);

        User updatedUser = userService.updateUser(userId, updateRequest);

        assertEquals("OriginalFirstName", updatedUser.getFirstName());
        assertEquals("OriginalLastName", updatedUser.getLastName());
        assertEquals("new@example.com", updatedUser.getEmail());
        assertEquals("originalPassword", updatedUser.getPassword());
    }

    @Test
    void updateUser_WithPasswordChanged_ShouldUpdateUser() {
        Long userId = 1L;
        User originalUser = new User();
        originalUser.setFirstName("OriginalFirstName");
        originalUser.setLastName("OriginalLastName");
        originalUser.setEmail("original@example.com");
        originalUser.setPassword("originalPassword");

        UserUpdateRequest updateRequest = new UserUpdateRequest(
                "OriginalFirstName",
                "OriginalLastName",
                "original@example.com",
                "newPassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(originalUser));
        when(passwordEncoder.matches(updateRequest.password(), originalUser.getPassword())).thenReturn(false);
        when(userRepository.save(originalUser)).thenReturn(originalUser);

        User updatedUser = userService.updateUser(userId, updateRequest);

        assertEquals("OriginalFirstName", updatedUser.getFirstName());
        assertEquals("OriginalLastName", updatedUser.getLastName());
        assertEquals("original@example.com", updatedUser.getEmail());
        assertNotEquals("originalPassword", updatedUser.getPassword());
    }

    @Test
    void updateUser_WithNoFieldsChanged_ShouldThrowInvalidRequestException() {
        Long userId = 1L;
        User originalUser = new User();
        originalUser.setFirstName("OriginalFirstName");
        originalUser.setLastName("OriginalLastName");
        originalUser.setEmail("original@example.com");
        originalUser.setPassword("originalPassword");

        UserUpdateRequest updateRequest = new UserUpdateRequest(
                "OriginalFirstName",
                "OriginalLastName",
                "original@example.com",
                "originalPassword"
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(originalUser));
        when(passwordEncoder.matches(updateRequest.password(), originalUser.getPassword())).thenReturn(true);


        assertThrows(InvalidRequestException.class, () -> userService.updateUser(userId, updateRequest));
    }


    @Test
    void enableUser_WhenUserIsNotEnabled_ShouldEnableUser() {
        User user = new User();
        user.setEnabled(false);

        when(userRepository.save(user)).thenReturn(user);

        userService.enableUser(user);

        assertTrue(user.getEnabled());
    }

    @Test
    void enableUser_WhenUserIsAlreadyEnabled_ShouldThrowIllegalStateException() {
        User user = new User();
        user.setEnabled(true);

        assertThrows(IllegalStateException.class, () -> userService.enableUser(user));
    }

    @Test
    void existsById_WithExistingUserId_ShouldReturnTrue() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        boolean exists = userService.existsById(userId);

        assertTrue(exists);
    }

    @Test
    void existsById_WithNonExistingUserId_ShouldReturnFalse() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        boolean exists = userService.existsById(userId);

        assertFalse(exists);
    }

    @Test
    void getReferenceById_WithExistingUserId_ShouldReturnUserReference() {
        Long userId = 1L;
        User user = new User();
        when(userRepository.existsById(userId)).thenReturn(true);
        when(userRepository.getReferenceById(userId)).thenReturn(user);

        User result = userService.getReferenceById(userId);

        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void getReferenceById_WithNonExistingUserId_ShouldThrowEntityNotFoundException() {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> userService.getReferenceById(userId));
    }

}