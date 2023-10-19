package by.zhukovsky.expensestracker.service;

import by.zhukovsky.expensestracker.dto.TransactionRequest;
import by.zhukovsky.expensestracker.entity.Category;
import by.zhukovsky.expensestracker.entity.Transaction;
import by.zhukovsky.expensestracker.entity.User;
import by.zhukovsky.expensestracker.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TransactionService {
    private final UserService userService;
    private final CategoryService categoryService;
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              UserService userService,
                              CategoryService categoryService) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    public Page<Transaction> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction with id " + id + " not found"));
    }

    public Transaction createTransaction(TransactionRequest request) {
        User user = userService.getUserById(request.userId());
        Category category = categoryService.getCategoryById(request.categoryId());

        Transaction transaction = new Transaction(request.type(),
                request.amount(),
                request.dateAt(),
                request.description(),
                user,
                category);

        transactionRepository.save(transaction);
        return transaction;
    }

    public Transaction updateTransaction(Long id, TransactionRequest request) {
        Transaction transaction = getTransactionById(id);

        if (transaction.getCategory().getId() != request.categoryId()) {
            Category category = categoryService.getCategoryById(request.categoryId());
            transaction.setCategory(category);
        }

        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setDate(request.dateAt());

        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new EntityNotFoundException("Transaction with id " + id + " not found");
        }
        transactionRepository.deleteById(id);
    }

    public Page<Transaction> getTransactionsForUser(
            Long userId,
            Pageable pageable,
            LocalDate startDate,
            LocalDate endDate,
            Long categoryId
    ) {
        if (!userService.existsById(userId)) {
            throw new EntityNotFoundException("User with id '" + userId + "' not found");
        }

        if (startDate != null && endDate != null) {
            if (categoryId != null) {
                return transactionRepository.findByUserIdAndDateBetweenAndCategoryId(userId,
                        startDate, endDate, categoryId, pageable);
            }
            return transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate, pageable);
        } else if (categoryId != null) {
            return transactionRepository.findByUserIdAndCategoryId(userId, categoryId, pageable);
        } else {
            return transactionRepository.findByUserId(userId, pageable);
        }
    }
}
