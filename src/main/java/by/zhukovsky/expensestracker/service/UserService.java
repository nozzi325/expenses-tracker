package by.zhukovsky.expensestracker.service;

import by.zhukovsky.expensestracker.dto.request.UserUpdateRequest;
import by.zhukovsky.expensestracker.entity.user.User;
import by.zhukovsky.expensestracker.exception.InvalidRequestException;
import by.zhukovsky.expensestracker.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
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
        if (userRepository.existsByEmailEqualsIgnoreCase(user.getEmail())) {
            throw new EntityExistsException("User with email '" + user.getEmail() + "' already exists");
        }
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
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityExistsException("User with email '" + email + "'not found"));
    }

    public User updateUser(Long id, UserUpdateRequest updateRequest) {
        User originalUser = getUserById(id);
        boolean fieldsChanged = false;

        if (!originalUser.getFirstName().equals(updateRequest.firstName())) {
            originalUser.setFirstName(updateRequest.firstName());
            fieldsChanged = true;
        }
        if (!originalUser.getLastName().equals(updateRequest.lastName())) {
            originalUser.setLastName(updateRequest.lastName());
            fieldsChanged = true;
        }
        if (!originalUser.getEmail().equals(updateRequest.email())) {
            if (userRepository.existsByEmailEqualsIgnoreCase(updateRequest.email())) {
                throw new EntityExistsException("Email '" + updateRequest.email() + "' is already in use");
            }
            originalUser.setEmail(updateRequest.email());
            fieldsChanged = true;
        }
        String newPassword = updateRequest.password();
        if (!passwordEncoder.matches(newPassword, originalUser.getPassword())) {
            originalUser.setPassword(passwordEncoder.encode(newPassword));
            fieldsChanged = true;
        }

        if (fieldsChanged) {
            return userRepository.save(originalUser);
        } else {
            throw new InvalidRequestException("No fields were changed");
        }
    }

    public void enableUser(User user) {
        if (user.getEnabled()) {
            throw new IllegalStateException("User already enabled");
        }
        user.setEnabled(true);
        userRepository.save(user);
    }

    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }

    public User getReferenceById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id " + userId + " not found");
        }
        return userRepository.getReferenceById(userId);
    }
}
