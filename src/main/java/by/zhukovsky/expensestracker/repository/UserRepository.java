package by.zhukovsky.expensestracker.repository;

import by.zhukovsky.expensestracker.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    boolean existsByEmailEqualsIgnoreCase(String email);
}
