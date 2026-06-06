package com.ph.core.story.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoHtmlValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoHtml {

    String message() default "Invalid input: HTML content is not allowed";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}