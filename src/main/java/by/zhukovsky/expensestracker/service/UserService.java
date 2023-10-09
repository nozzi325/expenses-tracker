package by.zhukovsky.expensestracker.service;

import by.zhukovsky.expensestracker.entity.user.User;
import by.zhukovsky.expensestracker.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        boolean emailTaken = userRepository.existsByEmailEqualsIgnoreCase(user.getEmail());
        if (emailTaken) {
            throw new EntityExistsException("User with email '" + user.getEmail() + "' already exists");
        }
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUser(Long id, User user) {
       User originalUser = getUserById(id);
       originalUser.setPassword(passwordEncoder.encode(user.getPassword()));
       originalUser.setEmail(user.getEmail());
       return userRepository.save(originalUser);
    }

    public void enableUser(User user) {
        if (user.getEnabled()) {
            throw new IllegalStateException("User already enabled");
        }
        user.setEnabled(true);
        userRepository.save(user);
    }
}
