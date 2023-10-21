package by.zhukovsky.expensestracker.dto;

public record AuthenticationResponse(
        String token,
        UserDTO user
) {
}