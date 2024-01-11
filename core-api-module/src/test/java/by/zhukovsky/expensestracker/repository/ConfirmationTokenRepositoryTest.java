package by.zhukovsky.expensestracker.repository;

import by.zhukovsky.expensestracker.entity.ConfirmationToken;
import by.zhukovsky.expensestracker.entity.user.User;
import by.zhukovsky.expensestracker.entity.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
        "spring.test.database.replace=none",
        "spring.datasource.url=jdbc:tc:postgresql:15-alpine:///tokens"
})
class ConfirmationTokenRepositoryTest {

    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        User testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("McClane");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setUserRole(UserRole.USER_ROLE);
        testUser.setEnabled(true);
        userRepository.save(testUser);

        ConfirmationToken token = new ConfirmationToken(
                "test-link", LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), testUser);
        confirmationTokenRepository.save(token);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        confirmationTokenRepository.deleteAll();
    }

    @Test
    public void testFindByToken_WhenTokenExists_ShouldReturnToken() {
        Optional<ConfirmationToken> foundToken = confirmationTokenRepository.findByToken("test-link");
        assertTrue(foundToken.isPresent());
        assertEquals("test-link", foundToken.get().getToken());
    }

    @Test
    public void testFindByToken_WhenTokenDoesNotExist_ShouldReturnEmpty() {
        Optional<ConfirmationToken> foundToken = confirmationTokenRepository.findByToken("nonexistent-link");
        assertFalse(foundToken.isPresent());
    }
}