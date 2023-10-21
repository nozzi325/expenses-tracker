package by.zhukovsky.expensestracker.dto;

public record AuthenticationRequest(
        String username,
        String password
) {
}