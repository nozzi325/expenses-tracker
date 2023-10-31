package by.zhukovsky.expensestracker.repository;

import by.zhukovsky.expensestracker.entity.Category;
import by.zhukovsky.expensestracker.entity.transaction.Transaction;
import by.zhukovsky.expensestracker.entity.transaction.TransactionType;
import by.zhukovsky.expensestracker.entity.user.User;
import by.zhukovsky.expensestracker.entity.user.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest(properties = {
        "spring.test.database.replace=none",
        "spring.datasource.url=jdbc:tc:postgresql:15-alpine:///transactions"
})
class TransactionRepositoryTest {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    private User testUser;
    private Category testCategory;

    @BeforeEach
    public void setUp() {
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("McClane");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setUserRole(UserRole.USER_ROLE);
        testUser.setEnabled(true);
        userRepository.save(testUser);

        testCategory = new Category();
        testCategory.setName("TestCategory");
        categoryRepository.save(testCategory);

        for (int i = 1; i <= 10; i++) {
            LocalDate transactionDate = LocalDate.now().minusDays(i);
            Transaction transaction = new Transaction(
                    TransactionType.EXPENSE,
                    100.0,
                    transactionDate,
                    "Test Transaction " + i,
                    testUser,
                    testCategory
            );
            transactionRepository.save(transaction);
        }
    }

    @AfterEach
    public void tearDown() {
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testFindTransactions_ByUserId() {
        Page<Transaction> transactions = transactionRepository.findByUserId(
                testUser.getId(), PageRequest.of(0, 10));

        assertNotNull(transactions);
        assertEquals(10, transactions.getTotalElements());
    }

    @Test
    public void testFindTransactions_ByUserIdAndDateRangeAndCategoryId() {
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now();

        Page<Transaction> transactions = transactionRepository.findByUserIdAndDateBetweenAndCategoryId(
                testUser.getId(),
                startDate,
                endDate,
                testCategory.getId(),
                PageRequest.of(0, 10)
        );

        assertNotNull(transactions);
        assertEquals(5, transactions.getTotalElements());
    }

    @Test
    public void testFindTransactions_ByUserIdAndDateRange() {
        LocalDate startDate = LocalDate.now().minusDays(5);
        LocalDate endDate = LocalDate.now();

        Page<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(
                testUser.getId(),
                startDate,
                endDate,
                PageRequest.of(0, 10)
        );

        assertNotNull(transactions);
        assertEquals(5, transactions.getTotalElements());
    }

    @Test
    public void testFindTransactions_ByUserIdAndCategoryId() {
        Page<Transaction> transactions = transactionRepository.findByUserIdAndCategoryId(
                testUser.getId(),
                testCategory.getId(),
                PageRequest.of(0, 10)
        );

        assertNotNull(transactions);
        assertEquals(10, transactions.getTotalElements());
    }
}