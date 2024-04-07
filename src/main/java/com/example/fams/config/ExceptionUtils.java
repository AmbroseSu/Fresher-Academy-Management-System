package com.example.fams.config;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.experimental.UtilityClass;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ExceptionUtils {
    public static final String DEFAULT_UNEXPECTED_MESSAGE = "Ops! Something wrong happens...";
    public List<String> getErrors(ConstraintViolationException exception){
        return exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
    }

    public static List<String> getErrors(RuntimeException exception) {
        return List.of(DEFAULT_UNEXPECTED_MESSAGE);
    }

    public static List<String> getErrors(MethodArgumentNotValidException exception) {
        return exception.getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
    }

    public static List<String> getResponseString(String response) {
        return List.of(response);
    }

    public static List<String> getError(String error) {
        return List.of(error);
    }


}
