package by.zhukovsky.expensestracker.dto.response;

import java.time.LocalDateTime;

public record ErrorResponse(String message, LocalDateTime timestamp) {
}
