package by.zhukovsky.expensestracker.dto.request;

public record UserUpdateRequest(
        String firstName,
        String lastName,
        String email,
        String password
) {
}