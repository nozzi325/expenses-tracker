package by.zhukovsky.expensestracker.service;

import by.zhukovsky.expensestracker.dto.request.TransactionRequest;
import by.zhukovsky.expensestracker.entity.Category;
import by.zhukovsky.expensestracker.entity.transaction.Transaction;
import by.zhukovsky.expensestracker.entity.transaction.TransactionType;
import by.zhukovsky.expensestracker.entity.user.User;
import by.zhukovsky.expensestracker.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @InjectMocks
    private TransactionService transactionService;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserService userService;
    @Mock
    private CategoryService categoryService;

    @Test
    void getTransactionById_WithExistingTransaction_ShouldReturnTransaction() {
        Long transactionId = 1L;
        Transaction transaction = new Transaction();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.getTransactionById(transactionId);
        assertEquals(transaction, result);
    }

    @Test
    void getTransactionById_WithNonExistingTransaction_ShouldThrowEntityNotFoundException() {
        Long transactionId = 1L;
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.getTransactionById(transactionId));
    }

    @Test
    void getAllTransactions_ShouldReturnAllTransactions() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> mockPage = Page.empty();

        when(transactionRepository.findAll(pageable)).thenReturn(mockPage);

        Page<Transaction> result = transactionService.getAllTransactions(pageable);

        assertEquals(mockPage, result);
    }

    @Test
    void createTransaction_ShouldCreateTransaction() {
        TransactionRequest request = new TransactionRequest(
                TransactionType.INCOME,
                100.0,
                LocalDate.now(),
                "Income description",
                1L,
                2L
        );

        User user = new User();
        Category category = new Category();
        Transaction transaction = new Transaction(
                TransactionType.INCOME,
                100.0,
                LocalDate.now(),
                "Income description",
                user,
                category
        );

        when(userService.getReferenceById(request.userId())).thenReturn(user);
        when(categoryService.getReferenceById(request.categoryId())).thenReturn(category);
        when(transactionRepository.save(any())).thenReturn(transaction);

        Transaction result = transactionService.createTransaction(request);
        assertEquals(user, result.getUser());
        assertEquals(category, result.getCategory());
        assertEquals(100.0, result.getAmount());
        assertEquals(TransactionType.INCOME, result.getType());
    }

    @Test
    void updateTransaction_WithExistingTransaction_ShouldUpdateTransaction() {
        Long transactionId = 1L;
        TransactionRequest request = new TransactionRequest(
                TransactionType.EXPENSE,
                50.0,
                LocalDate.now(),
                "Expense description",
                2L,
                3L
        );
        User user = new User();
        Category oldCategory = new Category();
        oldCategory.setId(2L);
        Category newCategory = new Category();
        newCategory.setId(3L);

        Transaction existingTransaction = new Transaction(
                TransactionType.EXPENSE,
                45.0,
                LocalDate.now(),
                "Expense description",
                user,
                oldCategory
        );

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(existingTransaction));
        when(categoryService.getCategoryById(request.categoryId())).thenReturn(newCategory);
        when(transactionRepository.save(existingTransaction)).thenReturn(existingTransaction);

        Transaction result = transactionService.updateTransaction(transactionId, request);
        assertEquals(request.categoryId(), result.getCategory().getId());
        assertEquals(request.amount(), result.getAmount());
    }

    @Test
    void updateTransaction_WithExistingTransactionAndSameCategory_ShouldUpdateTransaction() {
        Long transactionId = 1L;
        TransactionRequest request = new TransactionRequest(
                TransactionType.EXPENSE,
                50.0,
                LocalDate.now(),
                "Expense description",
                2L,
                2L
        );
        User user = new User();
        Category category = new Category();
        category.setId(2L);

        Transaction existingTransaction = new Transaction(
                TransactionType.EXPENSE,
                45.0,
                LocalDate.now(),
                "Expense description",
                user,
                category
        );

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(existingTransaction)).thenReturn(existingTransaction);

        Transaction result = transactionService.updateTransaction(transactionId, request);
        assertEquals(request.categoryId(), result.getCategory().getId());
        assertEquals(request.amount(), result.getAmount());
    }

    @Test
    void updateTransaction_WithNonExistingTransaction_ShouldThrowEntityNotFoundException() {
        Long transactionId = 1L;
        TransactionRequest request = new TransactionRequest(
                TransactionType.EXPENSE,
                50.0,
                LocalDate.now(),
                "Expense description",
                2L,
                3L
        );

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.updateTransaction(transactionId, request));
    }

    @Test
    void deleteTransaction_WithExistingTransaction_ShouldDeleteTransaction() {
        Long transactionId = 1L;

        when(transactionRepository.existsById(transactionId)).thenReturn(true);

        assertDoesNotThrow(() -> transactionService.deleteTransaction(transactionId));
    }

    @Test
    void deleteTransaction_WithNonExistingTransaction_ShouldThrowEntityNotFoundException() {
        Long transactionId = 1L;

        when(transactionRepository.existsById(transactionId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> transactionService.deleteTransaction(transactionId));
    }

    // Test for the case when all parameters are specified.
    @Test
    void getTransactionsForUser_WithAllParameters_ShouldUseFindByUserIdAndDateBetweenAndCategoryId() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        Long categoryId = 2L;
        Pageable pageable = PageRequest.of(0, 10);

        when(userService.existsById(userId)).thenReturn(true);
        when(transactionRepository.findByUserIdAndDateBetweenAndCategoryId(
                userId, startDate, endDate, categoryId, pageable
        )).thenReturn(Page.empty());

        transactionService.getTransactionsForUser(
                userId,
                pageable,
                startDate,
                endDate,
                categoryId
        );

        verify(transactionRepository)
                .findByUserIdAndDateBetweenAndCategoryId(userId, startDate, endDate, categoryId, pageable);
    }

    // Test for the case when only start date and end date are specified.
    @Test
    void getTransactionsForUser_WithStartDateAndEndDate_ShouldCallFindByUserIdAndDateBetween() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        Pageable pageable = PageRequest.of(0, 10);

        when(userService.existsById(userId)).thenReturn(true);
        when(transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate, pageable))
                .thenReturn(Page.empty());

        transactionService.getTransactionsForUser(
                userId,
                pageable,
                startDate,
                endDate,
                null
        );

        verify(transactionRepository).findByUserIdAndDateBetween(userId, startDate, endDate, pageable);
    }

    // Test for the case when only start date and categoryId are specified.
    @Test
    void getTransactionsForUser_WithStartDateOnlyAndCategoryId_ShouldCallFindByUserIdAndCategoryId() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        Long categoryId = 2L;
        Pageable pageable = PageRequest.of(0, 10);

        when(userService.existsById(userId)).thenReturn(true);
        when(transactionRepository.findByUserIdAndCategoryId(userId, categoryId, pageable))
                .thenReturn(Page.empty());

        transactionService.getTransactionsForUser(
                userId,
                pageable,
                startDate,
                null,
                categoryId
        );

        verify(transactionRepository).findByUserIdAndCategoryId(userId, categoryId, pageable);
    }

    // Test for the case when only end date and categoryId are specified.
    @Test
    void getTransactionsForUser_WithEndDateOnlyAndCategoryId_ShouldCallFindByUserIdAndCategoryId() {
        Long userId = 1L;
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        Long categoryId = 2L;
        Pageable pageable = PageRequest.of(0, 10);

        when(userService.existsById(userId)).thenReturn(true);
        when(transactionRepository.findByUserIdAndCategoryId(userId, categoryId, pageable))
                .thenReturn(Page.empty());

        transactionService.getTransactionsForUser(
                userId,
                pageable,
                null,
                endDate,
                categoryId
        );

        verify(transactionRepository).findByUserIdAndCategoryId(userId, categoryId, pageable);
    }

    // Test for the case when only start date is specified.
    @Test
    void getTransactionsForUser_WithStartDateOnly_ShouldCallFindByUserId() {
        Long userId = 1L;
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        Pageable pageable = PageRequest.of(0, 10);

        when(userService.existsById(userId)).thenReturn(true);
        when(transactionRepository.findByUserId(userId, pageable)).thenReturn(Page.empty());

        transactionService.getTransactionsForUser(
                userId,
                pageable,
                startDate,
                null,
                null
        );

        verify(transactionRepository).findByUserId(userId, pageable);
    }

    // Test for the case when only end date is specified.
    @Test
    void getTransactionsForUser_WithEndDateOnly_ShouldShouldCallFindByUserId() {
        Long userId = 1L;
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        Pageable pageable = PageRequest.of(0, 10);

        when(userService.existsById(userId)).thenReturn(true);
        when(transactionRepository.findByUserId(userId, pageable)).thenReturn(Page.empty());

        transactionService.getTransactionsForUser(
                userId,
                pageable,
                null,
                endDate,
                null
        );

        verify(transactionRepository).findByUserId(userId, pageable);
    }

    // Test for the case when only categoryId is specified.
    @Test
    void getTransactionsForUser_WithCategoryId_ShouldCallFindByUserIdAndCategoryId() {
        Long userId = 1L;
        Long categoryId = 2L;
        Pageable pageable = PageRequest.of(0, 10);

        when(userService.existsById(userId)).thenReturn(true);
        when(transactionRepository.findByUserIdAndCategoryId(userId, categoryId, pageable))
                .thenReturn(Page.empty());

        transactionService.getTransactionsForUser(
                userId,
                pageable,
                null,
                null,
                categoryId
        );

        verify(transactionRepository).findByUserIdAndCategoryId(userId, categoryId, pageable);
    }

    // Test for the case when only userId is specified.
    @Test
    void getTransactionsForUser_WithUserId_ShouldCallFindByUserId() {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        when(userService.existsById(userId)).thenReturn(true);
        when(transactionRepository.findByUserId(userId, pageable)).thenReturn(Page.empty());

        transactionService.getTransactionsForUser(
                userId,
                pageable,
                null,
                null,
                null
        );

        verify(transactionRepository).findByUserId(userId, pageable);
    }

    // Test for the case when the specified user does not exist, and an EntityNotFoundException should be thrown.
    @Test
    void getTransactionsForUser_WithInvalidUserId_ShouldThrowEntityNotFoundException() {
        Long userId = 1L;
        when(userService.existsById(userId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> transactionService.getTransactionsForUser(
                        userId, Pageable.unpaged(), null, null, null));
    }
}