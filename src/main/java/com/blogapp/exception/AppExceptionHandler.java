package com.blogapp.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.persistence.EntityNotFoundException;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorMessages> handleNotFoundExceptions(EntityNotFoundException entityNotFoundException) {

        return responseErrorMessages(List.of(entityNotFoundException.getMessage()), HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorMessages> handleNotFoundExceptions(BadCredentialsException runtimeException) {

        return responseErrorMessages(List.of(runtimeException.getMessage()), HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessages> handleNotFoundExceptions(RuntimeException runtimeException) {

        return responseErrorMessages(List.of(runtimeException.getMessage()), HttpStatus.BAD_REQUEST);

    }

    private ResponseEntity<ErrorMessages> responseErrorMessages(List<String> messages, HttpStatus status) {
        ErrorMessages errorMessages = new ErrorMessages();
        messages.forEach(errorMessages::append);
        return new ResponseEntity<>(errorMessages, status);
    }
}
