package by.zhukovsky.expensestracker.utils;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

public class ExceptionHandlingUtils {
    public static EntityNotFoundException handleEntityNotFound(String entityType, Long id) {
        throw new EntityNotFoundException(entityType + " with ID " + id + " not found");
    }

    public static EntityExistsException handleEntityAlreadyExists(String entityType, String name) {
        throw new EntityExistsException(entityType + " '" + name + "' already exists");
    }
}