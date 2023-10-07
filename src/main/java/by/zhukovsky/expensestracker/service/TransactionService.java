package by.zhukovsky.expensestracker.service;

import by.zhukovsky.expensestracker.dto.TransactionRequest;
import by.zhukovsky.expensestracker.entity.Transaction;
import by.zhukovsky.expensestracker.entity.Category;
import by.zhukovsky.expensestracker.entity.User;
import by.zhukovsky.expensestracker.repository.CategoryRepository;
import by.zhukovsky.expensestracker.repository.TransactionRepository;
import by.zhukovsky.expensestracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository,
                              CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction with id " + id + " not found"));
    }

    public Transaction createTransaction(TransactionRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new EntityNotFoundException("User with id " + request.userId() + " not found"));
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + request.categoryId() + " not found"));

        Transaction transaction = new Transaction(request.type(), request.amount(),
                request.dateAt(), request.description(),
                user, category);

        transactionRepository.save(transaction);
        return transaction;
    }

    public Transaction updateTransaction(Long id, TransactionRequest request) {
        Transaction transaction = getTransactionById(id);

        if (transaction.getCategory().getId() != request.categoryId()) {
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category with id " + request.categoryId() + " not found"));
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

}
