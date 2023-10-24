package by.zhukovsky.expensestracker.service;

import by.zhukovsky.expensestracker.dto.request.TransactionRequest;
import by.zhukovsky.expensestracker.entity.Category;
import by.zhukovsky.expensestracker.entity.transaction.Transaction;
import by.zhukovsky.expensestracker.entity.user.User;
import by.zhukovsky.expensestracker.repository.TransactionRepository;
import by.zhukovsky.expensestracker.utils.ExceptionHandlingUtils;
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
                .orElseThrow(() -> ExceptionHandlingUtils.handleEntityNotFound("Transaction", id));
    }

    public Transaction createTransaction(TransactionRequest request) {
        User user = userService.getReferenceById(request.userId());
        Category category = categoryService.getReferenceById(request.categoryId());

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
            ExceptionHandlingUtils.handleEntityNotFound("Transaction", id);
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
            ExceptionHandlingUtils.handleEntityNotFound("User", userId);
        }

        if (startDate != null && endDate != null) {
            if (categoryId != null) {
                return transactionRepository.findByUserIdAndDateBetweenAndCategoryId(
                        userId,
                        startDate,
                        endDate,
                        categoryId,
                        pageable
                );
            }
            return transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate, pageable);
        } else if (categoryId != null) {
            return transactionRepository.findByUserIdAndCategoryId(userId, categoryId, pageable);
        } else {
            return transactionRepository.findByUserId(userId, pageable);
        }
    }
}
