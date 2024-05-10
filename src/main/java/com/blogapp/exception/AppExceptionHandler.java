package com.blogapp.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorMessages> handleNotFoundExceptions(AppException appException) {

        return responseErrorMessages(List.of(appException.getMessage()), appException.getError().getStatus());

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessages> handleNotFoundExceptions(BadCredentialsException runtimeException) {

        return responseErrorMessages(List.of(runtimeException.getMessage()), HttpStatus.UNAUTHORIZED);

    }

    // @ExceptionHandler(RuntimeException.class)
    // public ResponseEntity<ErrorMessages>
    // handleNotFoundExceptions(RuntimeException runtimeException) {

    // return responseErrorMessages(List.of(runtimeException.getMessage()),
    // HttpStatus.BAD_REQUEST);

    // }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessages> handleValidationError(MethodArgumentNotValidException exception) {
        List<String> messages = exception.getBindingResult().getFieldErrors().stream()
                .map(this::createFieldErrorMessage).collect(Collectors.toList());
        return responseErrorMessages(messages, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    private String createFieldErrorMessage(FieldError fieldError) {
        return "[" +
                fieldError.getField() +
                "] must be " +
                fieldError.getDefaultMessage() +
                ". your input: [" +
                fieldError.getRejectedValue() +
                "]";
    }
    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<ErrorMessages> handleException(Exception exception) {
    // return responseErrorMessages(List.of("internal server error"),
    // HttpStatus.UNPROCESSABLE_ENTITY);
    // }

    private ResponseEntity<ErrorMessages> responseErrorMessages(List<String> messages, HttpStatus status) {
        ErrorMessages errorMessages = new ErrorMessages();
        messages.forEach(errorMessages::append);
        return new ResponseEntity<>(errorMessages, status);
    }
}
