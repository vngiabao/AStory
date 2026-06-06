package com.ph.core.story.common.validation;

import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StrongPasswordValidator.class)
public @interface StrongPassword {

    String message() default "Password must be at least 12 characters and include at least one uppercase letter, one lowercase letter, one number, and one special character (!@#$%^&*()).";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
