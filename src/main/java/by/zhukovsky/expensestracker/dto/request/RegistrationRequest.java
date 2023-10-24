package by.zhukovsky.expensestracker.dto.request;

public record RegistrationRequest(
        String email,
        String password,
        String firstName,
        String lastName
) {
}
