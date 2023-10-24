package by.zhukovsky.expensestracker.dto.response;

public record AuthenticationResponse(
        String token,
        UserDTO user
) {
}