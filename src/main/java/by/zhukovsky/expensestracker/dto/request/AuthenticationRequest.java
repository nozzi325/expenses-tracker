package by.zhukovsky.expensestracker.dto.request;

public record AuthenticationRequest(
        String username,
        String password
) {
}