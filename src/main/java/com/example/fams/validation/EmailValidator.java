package com.example.fams.validation;

//import javax.validation.ConstraintValidator;
//import javax.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    private static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";

    private String nullMessage;
    private String invalidFormatMessage;
    private int nullIntegerValue;
    private int invalidIntegerValue;

    @Override
    public void initialize(ValidEmail constraintAnnotation) {
        nullMessage = constraintAnnotation.nullMessage();
        invalidFormatMessage = constraintAnnotation.message();
        nullIntegerValue = constraintAnnotation.nullIntegerValue();
        invalidIntegerValue = constraintAnnotation.invalidIntegerValue();
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isEmpty()) {
            handleValidationFailure(context, nullMessage, nullIntegerValue);
            return false;
        }

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            handleValidationFailure(context, invalidFormatMessage, invalidIntegerValue);
            return false;
        }

        return true;
    }

    private void handleValidationFailure(
            ConstraintValidatorContext context,
            String message,
            int integerValue
    ) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode("integerValue")  // Adjust to your actual property name
                .addConstraintViolation();
    }
}
