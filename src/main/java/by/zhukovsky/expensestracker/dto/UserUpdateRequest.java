package by.zhukovsky.expensestracker.dto;

public record UserUpdateRequest(
        String firstName,
        String lastName,
        String email,
        String password
) {
}