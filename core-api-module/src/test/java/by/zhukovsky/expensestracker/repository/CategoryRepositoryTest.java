package by.zhukovsky.expensestracker.repository;

import by.zhukovsky.expensestracker.entity.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
        "spring.test.database.replace=none",
        "spring.datasource.url=jdbc:tc:postgresql:15-alpine:///categories"
})
class CategoryRepositoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setUp() {
        Category category = new Category();
        category.setName("TestCategory");
        categoryRepository.save(category);
    }

    @AfterEach
    public void tearDown() {
        categoryRepository.deleteAll();
    }

    @Test
    public void testExistsCategoryByNameEqualsIgnoreCase_WhenCategoryExists_ShouldReturnTrue() {
        boolean exists = categoryRepository.existsCategoryByNameEqualsIgnoreCase("TestCategory");
        assertTrue(exists);
    }

    @Test
    public void testExistsCategoryByNameEqualsIgnoreCase_WhenCategoryDoesNotExist_ShouldReturnFalse() {
        boolean exists = categoryRepository.existsCategoryByNameEqualsIgnoreCase("NonExistentCategory");
        assertFalse(exists);
    }
}