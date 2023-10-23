package by.zhukovsky.expensestracker.service;

import by.zhukovsky.expensestracker.entity.Category;
import by.zhukovsky.expensestracker.repository.CategoryRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<Category> getAllCategories() {
        return repository.findAll();
    }

    public Category getCategoryById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with " + id + " not found"));
    }

    public Category createCategory(Category category) {
        String name = category.getName();
        if (repository.existsCategoryByNameEqualsIgnoreCase(name)) {
            throw new EntityExistsException("Category '" + name + "' already exists");
        }
        return repository.save(category);
    }

    public Category updateCategory(Long id, Category category) {
        Category originalCategory = getCategoryById(id);
        originalCategory.setDescription(category.getDescription());
        originalCategory.setName(category.getName());
        return repository.save(originalCategory);
    }

    public void deleteCategory(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Category with id " + id + " not found");
        }
        repository.deleteById(id);
    }

    public Category getReferenceById(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Category with id " + id + " not found");
        }
        Category category = repository.getReferenceById(id);
        return category;
    }
}
