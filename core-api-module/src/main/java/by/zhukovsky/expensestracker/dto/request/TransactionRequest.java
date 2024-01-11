package by.zhukovsky.expensestracker.dto.request;

import by.zhukovsky.expensestracker.entity.transaction.TransactionType;

import java.time.LocalDate;

public record TransactionRequest (
        TransactionType type,
        double amount,
        LocalDate dateAt,
        String description,
        Long userId,
        Long categoryId
) {
}