package com.lkgroup.ecommerce.common.validation.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

//@Component
public class PathUUIDValidator implements ConstraintValidator<PathUUID, String> {
    private static final Pattern pattern = Pattern.compile("^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$", 2);

    @Override
    public boolean isValid(final String uuid, final ConstraintValidatorContext context) {
        return uuid == null || this.pattern.matcher(uuid).matches();
    }
}
