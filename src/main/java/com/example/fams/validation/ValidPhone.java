package com.example.fams.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhone {
    String message() default "Invalid phone format";
    String nullMessage() default "phone cannot be null";
    int nullIntegerValue() default 2;
    int invalidIntegerValue() default 3;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
