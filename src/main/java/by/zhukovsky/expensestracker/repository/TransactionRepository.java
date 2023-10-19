package by.zhukovsky.expensestracker.repository;

import by.zhukovsky.expensestracker.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByUserId(Long userId, Pageable pageable);

    Page<Transaction> findByUserIdAndDateBetweenAndCategoryId(Long userId,
                                                              LocalDate startDate,
                                                              LocalDate endDate,
                                                              Long categoryId,
                                                              Pageable pageable);

    Page<Transaction> findByUserIdAndDateBetween(Long userId, LocalDate startDate,
                                                 LocalDate endDate, Pageable pageable);

    Page<Transaction> findByUserIdAndCategoryId(Long userId, Long categoryId, Pageable pageable);
}
