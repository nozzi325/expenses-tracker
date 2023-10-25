package by.zhukovsky.expensestracker.service;

import by.zhukovsky.expensestracker.entity.Category;
import by.zhukovsky.expensestracker.repository.CategoryRepository;
import by.zhukovsky.expensestracker.utils.ExceptionHandlingUtils;
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
                .orElseThrow(() -> ExceptionHandlingUtils.handleEntityNotFound("Category", id));
    }

    public Category createCategory(Category category) {
        String name = category.getName();
        if (repository.existsCategoryByNameEqualsIgnoreCase(name)) {
            throw ExceptionHandlingUtils.handleEntityAlreadyExists("Category", name);
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
            ExceptionHandlingUtils.handleEntityNotFound("Category", id);
        }
        repository.deleteById(id);
    }

    public Category getReferenceById(Long id) {
        if (!repository.existsById(id)) {
            ExceptionHandlingUtils.handleEntityNotFound("Category", id);
        }
        return repository.getReferenceById(id);
    }
}
