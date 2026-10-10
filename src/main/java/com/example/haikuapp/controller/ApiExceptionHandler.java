package com.example.haikuapp.controller;

import com.example.haikuapp.service.DuplicateLoginIdException;
import com.example.haikuapp.service.HaikuNotFoundException;
import com.example.haikuapp.service.InvalidCredentialsException;
import com.example.haikuapp.service.InvalidHaikuException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(DuplicateLoginIdException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateLoginId(
            DuplicateLoginIdException exception
    ) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(List.of(exception.getMessage())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception
    ) {
        List<String> messages = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .distinct()
                .toList();

        return ResponseEntity.badRequest()
                .body(new ErrorResponse(messages));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException exception
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(List.of(exception.getMessage())));
    }

    @ExceptionHandler(InvalidHaikuException.class)
    public ResponseEntity<ErrorResponse> handleInvalidHaiku(
            InvalidHaikuException exception
    ) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(List.of(exception.getMessage())));
    }

    @ExceptionHandler(LoginRequiredException.class)
    public ResponseEntity<ErrorResponse> handleLoginRequired(
            LoginRequiredException exception
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(List.of(exception.getMessage())));
    }

    @ExceptionHandler(HaikuNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleHaikuNotFound(
            HaikuNotFoundException exception
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(List.of(exception.getMessage())));
    }

    public record ErrorResponse(List<String> messages) {
    }
}