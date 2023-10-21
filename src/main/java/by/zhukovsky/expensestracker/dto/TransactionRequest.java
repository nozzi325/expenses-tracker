package by.zhukovsky.expensestracker.dto;

import by.zhukovsky.expensestracker.entity.transaction.TransactionType;

import java.util.Date;

public record TransactionRequest (
        TransactionType type,
        double amount,
        Date dateAt,
        String description,
        Long userId,
        Long categoryId
) {
}
