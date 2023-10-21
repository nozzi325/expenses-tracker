package by.zhukovsky.expensestracker.dto;

public record RegistrationRequest(
        String email,
        String password,
        String firstName,
        String lastName
) {
}
