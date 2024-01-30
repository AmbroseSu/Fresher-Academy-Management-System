package com.example.fams.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.security.core.parameters.P;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {
    private static final String PHONE_PATTERN = "^(\\+\\d{1,3}[- ]?)?\\(?\\d{1,6}\\)?[- ]?\\d{1,15}$";
    private String nullMessage;
    private String invalidFormatMessage;
    private int nullIntegerValue;
    private int invalidIntegerValue;

    @Override
    public void initialize(ValidPhone constraintAnnotation) {
        nullMessage = constraintAnnotation.nullMessage();
        invalidFormatMessage = constraintAnnotation.message();
        nullIntegerValue = constraintAnnotation.nullIntegerValue();
        invalidIntegerValue = constraintAnnotation.invalidIntegerValue();
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null) {
            handleValidationFailure(context, nullMessage, nullIntegerValue);
            return false;
        }

        Pattern pattern = Pattern.compile(PHONE_PATTERN);
        Matcher matcher = pattern.matcher(phone);

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
