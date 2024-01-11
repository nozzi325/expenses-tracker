package by.zhukovsky.expensestracker.service.auth;

import by.zhukovsky.expensestracker.dto.request.AuthenticationRequest;
import by.zhukovsky.expensestracker.dto.response.AuthenticationResponse;
import by.zhukovsky.expensestracker.dto.response.UserDTO;
import by.zhukovsky.expensestracker.entity.user.User;
import by.zhukovsky.expensestracker.jwt.JWTUtil;
import by.zhukovsky.expensestracker.mapper.UserDTOMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final UserDTOMapper userDTOMapper;


    public AuthenticationService(AuthenticationManager authenticationManager,
                                 JWTUtil jwtUtil,
                                 UserDTOMapper userDTOMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDTOMapper = userDTOMapper;
    }

    public AuthenticationResponse login(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );
        User user = (User) authentication.getPrincipal();
        UserDTO userDTO = userDTOMapper.apply(user);
        String token = jwtUtil.issueToken(userDTO.email(), userDTO.roles());
        return new AuthenticationResponse(token, userDTO);
    }
}