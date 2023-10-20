package by.zhukovsky.expensestracker.service;

import by.zhukovsky.expensestracker.entity.User;
import by.zhukovsky.expensestracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
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

    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    public User getReferenceById(Long userId) {
        User user = userRepository.getReferenceById(userId);
        if (user == null) {
            throw new EntityNotFoundException("User with id " + userId + " not found");
        }
        return user;
    }
}
