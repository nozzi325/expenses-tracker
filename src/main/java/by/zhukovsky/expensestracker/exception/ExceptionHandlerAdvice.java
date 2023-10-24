package by.zhukovsky.expensestracker.exception;

import by.zhukovsky.expensestracker.dto.response.ErrorResponse;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class ExceptionHandlerAdvice {
    Logger logger = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> exceptionEntityNotFoundHandler(EntityNotFoundException e) {
        ErrorResponse error = createErrorResponse(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorResponse> exceptionEntityExistsHandler(EntityExistsException e) {
        ErrorResponse error = createErrorResponse(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConfirmationTokenExpiredException.class)
    public ResponseEntity<ErrorResponse> exceptionConfirmationTokenExpiredHandler(ConfirmationTokenExpiredException e) {
        ErrorResponse error = createErrorResponse(e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.GONE);
    }

    private ErrorResponse createErrorResponse(String message) {
        logger.error(message);
        return new ErrorResponse(message, LocalDateTime.now());
    }
}
