package by.zhukovsky.expensestracker.controller;

import by.zhukovsky.expensestracker.dto.response.UserDTO;
import by.zhukovsky.expensestracker.dto.request.UserUpdateRequest;
import by.zhukovsky.expensestracker.entity.transaction.Transaction;
import by.zhukovsky.expensestracker.entity.user.User;
import by.zhukovsky.expensestracker.mapper.UserDTOMapper;
import by.zhukovsky.expensestracker.service.TransactionService;
import by.zhukovsky.expensestracker.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final TransactionService transactionService;
    private final UserDTOMapper userDTOMapper;

    public UserController(UserService userService,
                          TransactionService transactionService,
                          UserDTOMapper userDTOMapper) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.userDTOMapper = userDTOMapper;
    }

    @GetMapping
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable).map(userDTOMapper);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userDTOMapper.apply(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest updateRequest
    ) {
        User updatedUser = userService.updateUser(id, updateRequest);
        return ResponseEntity.ok(userDTOMapper.apply(updatedUser));
    }

    @GetMapping("/{id}/transactions")
    public Page<Transaction> getUserTransactions(
            @PathVariable Long id,
            Pageable pageable,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long categoryId
    ) {
        return transactionService.getTransactionsForUser(id, pageable, startDate, endDate, categoryId);
    }
}
