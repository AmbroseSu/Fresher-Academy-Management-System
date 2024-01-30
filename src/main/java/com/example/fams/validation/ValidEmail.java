package com.example.fams.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {
    String message() default "Invalid email format";
    String nullMessage() default "Email cannot be null";
    int nullIntegerValue() default 2;
    int invalidIntegerValue() default 3;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
