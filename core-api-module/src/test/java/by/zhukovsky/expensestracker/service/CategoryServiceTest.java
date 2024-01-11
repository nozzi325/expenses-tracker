package by.zhukovsky.expensestracker.service;

import by.zhukovsky.expensestracker.entity.Category;
import by.zhukovsky.expensestracker.repository.CategoryRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @InjectMocks
    private CategoryService categoryService;
    @Mock
    private CategoryRepository categoryRepository;

    @Test
    void getAllCategories_ShouldReturnListOfCategories() {
        List<Category> categories = List.of(
                new Category(1L, "Category 1", "Description 1"),
                new Category(2L, "Category 2", "Description 2")
        );

        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();
        assertEquals(categories, result);
    }

    @Test
    void getCategoryById_WithExistingCategory_ShouldReturnCategory() {
        Long categoryId = 1L;
        Category category = new Category(categoryId, "Category 1", "Description 1");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        Category result = categoryService.getCategoryById(categoryId);
        assertEquals(category, result);
    }

    @Test
    void getCategoryById_WithNonExistingCategory_ShouldThrowEntityNotFoundException() {
        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryById(categoryId));
    }

    @Test
    void createCategory_WithUniqueName_ShouldCreateCategory() {
        Category category = new Category(1L, "New Category", "New Description");

        when(categoryRepository.existsCategoryByNameEqualsIgnoreCase(category.getName())).thenReturn(false);
        when(categoryRepository.save(category)).thenReturn(category);

        Category createdCategory = categoryService.createCategory(category);
        assertEquals(category, createdCategory);
    }

    @Test
    void createCategory_WithExistingName_ShouldThrowEntityExistsException() {
        Category category = new Category(1L, "Existing Category", "Description");

        when(categoryRepository.existsCategoryByNameEqualsIgnoreCase(category.getName())).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> categoryService.createCategory(category));
    }

    @Test
    void updateCategory_WithExistingCategory_ShouldUpdateCategory() {
        Long categoryId = 1L;
        Category originalCategory = new Category(categoryId, "Original Category", "Original Description");
        Category updatedCategory = new Category(categoryId, "Updated Category", "Updated Description");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(originalCategory));
        when(categoryRepository.save(originalCategory)).thenReturn(updatedCategory);

        Category result = categoryService.updateCategory(categoryId, updatedCategory);
        assertEquals(updatedCategory, result);
    }

    @Test
    void updateCategory_WithNonExistingCategory_ShouldThrowEntityNotFoundException() {
        Long categoryId = 1L;
        Category updatedCategory = new Category(categoryId, "Updated Category", "Updated Description");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.updateCategory(categoryId, updatedCategory));
    }

    @Test
    void deleteCategory_WithExistingCategory_ShouldDeleteCategory() {
        Long categoryId = 1L;

        when(categoryRepository.existsById(categoryId)).thenReturn(true);

        categoryService.deleteCategory(categoryId);
    }

    @Test
    void deleteCategory_WithNonExistingCategory_ShouldThrowEntityNotFoundException() {
        Long categoryId = 1L;

        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> categoryService.deleteCategory(categoryId));
    }

    @Test
    void getReferenceById_WithExistingCategory_ShouldReturnCategoryReference() {
        Long categoryId = 1L;
        Category category = new Category(categoryId, "Category 1", "Description 1");

        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(categoryRepository.getReferenceById(categoryId)).thenReturn(category);

        Category result = categoryService.getReferenceById(categoryId);
        assertEquals(category, result);
    }

    @Test
    void getReferenceById_WithNonExistingCategory_ShouldThrowEntityNotFoundException() {
        Long categoryId = 1L;

        when(categoryRepository.existsById(categoryId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> categoryService.getReferenceById(categoryId));
    }

}