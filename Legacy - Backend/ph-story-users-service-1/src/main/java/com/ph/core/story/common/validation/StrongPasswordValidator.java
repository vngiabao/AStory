package com.ph.core.story.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {

        if (password == null)
            return false;

        return password.length() >= 12 && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*") && password.matches(".*\\d.*")
                && password.matches(".*[!@#$%^&*()].*");
    }
}
