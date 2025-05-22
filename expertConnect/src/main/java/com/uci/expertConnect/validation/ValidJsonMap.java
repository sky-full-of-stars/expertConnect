package com.uci.expertConnect.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = JsonMapValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidJsonMap {
    String message() default "Invalid JSON structure for availability";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
} 