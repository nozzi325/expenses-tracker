package by.zhukovsky.expensestracker.repository;

import by.zhukovsky.expensestracker.entity.user.User;
import by.zhukovsky.expensestracker.entity.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
        "spring.test.database.replace=none",
        "spring.datasource.url=jdbc:tc:postgresql:15-alpine:///users"
})
class UserRepositoryTest {
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
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void testFindByEmail_WhenUserExists_ShouldReturnUser() {
        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    public void testFindByEmail_WhenUserDoesNotExist_ShouldReturnEmpty() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");
        assertFalse(foundUser.isPresent());
    }

    @Test
    public void testExistsByEmailEqualsIgnoreCase_WhenUserExists_ShouldReturnTrue() {
        assertTrue(userRepository.existsByEmailEqualsIgnoreCase("Test@ExAmPlE.cOm"));
    }

    @Test
    public void testExistsByEmailEqualsIgnoreCase_WhenUserDoesNotExist_ShouldReturnFalse() {
        assertFalse(userRepository.existsByEmailEqualsIgnoreCase("nonexistent@example.com"));
    }

}