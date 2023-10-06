package by.zhukovsky.expensestracker.repository;

import by.zhukovsky.expensestracker.entity.TransactionCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionCategoryRepository extends JpaRepository<TransactionCategory, Long> {
}
