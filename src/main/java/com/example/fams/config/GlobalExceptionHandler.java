package com.example.fams.config;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errorMessages = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());
        return ResponseUtil.error(ExceptionUtils.getErrors(ex), "Bad request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        String errorMessage = ex.getMessage();
        return ResponseUtil.error(errorMessage, "Bad request", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MailException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleMailException(MailException ex) {
        String errorMessage = ex.getMessage();
        return ResponseUtil.error(errorMessage, "Bad request", HttpStatus.BAD_REQUEST);
    }

    // ! Lộc add thêm vào ngày 01/02/2024
//    @ExceptionHandler(ConstraintViolationException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException e) {
//        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
//        List<String> errorMessages = violations.stream()
//                .map(violation -> String.format("%s %s", violation.getPropertyPath(), violation.getMessage()))
//                .collect(Collectors.toList());
//
//        // Join error messages for a detailed error message
//        String detailedErrorMessage = String.join(", ", errorMessages);
//        // Use the first error message as a user-friendly message
//
//        // Return a ResponseEntity with an error using ResponseUtil
//        return ResponseUtil.error(detailedErrorMessage, "Bad request", HttpStatus.BAD_REQUEST);
//    }
}