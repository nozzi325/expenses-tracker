package by.zhukovsky.expensestracker.service.auth;

import by.zhukovsky.expensestracker.dto.request.AuthenticationRequest;
import by.zhukovsky.expensestracker.dto.response.AuthenticationResponse;
import by.zhukovsky.expensestracker.dto.response.UserDTO;
import by.zhukovsky.expensestracker.entity.user.User;
import by.zhukovsky.expensestracker.entity.user.UserRole;
import by.zhukovsky.expensestracker.jwt.JWTUtil;
import by.zhukovsky.expensestracker.mapper.UserDTOMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JWTUtil jwtUtil;
    @Mock
    private UserDTOMapper userDTOMapper;
    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void login_WithValidCredentials_ShouldReturnAuthenticationResponse() {
        AuthenticationRequest request = new AuthenticationRequest("username", "password");
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("McClane");
        user.setEmail("username");
        user.setUserRole(UserRole.USER_ROLE);
        user.setEnabled(true);
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);

        UserDTO userDTO = new UserDTO(
                1L,
                "John",
                "McClane",
                "username",
                user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()),
                true);
        String token = "mockToken";


        when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userDTOMapper.apply(user)).thenReturn(userDTO);
        when(jwtUtil.issueToken(userDTO.email(), userDTO.roles())).thenReturn(token);

        AuthenticationResponse response = authenticationService.login(request);

        assertEquals(token, response.token());
        assertEquals(userDTO, response.user());
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowRuntimeException() {
        AuthenticationRequest request = new AuthenticationRequest("john@example.com", "invalidPassword");
        when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid credentials"));

        assertThrows(RuntimeException.class, () -> authenticationService.login(request));
    }
}