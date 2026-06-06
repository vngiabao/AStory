package com.ph.core.story.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class NoHtmlValidator implements ConstraintValidator<NoHtml, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (value == null || value.isBlank()) {
            return true;
        }

        // Parse HTML
        Document doc = Jsoup.parseBodyFragment(value);

        // Nếu có element thực sự (tag) trong body → fail
        return doc.body().children().isEmpty();
    }
}