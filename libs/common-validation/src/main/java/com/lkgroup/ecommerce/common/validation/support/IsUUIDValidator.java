package com.lkgroup.ecommerce.common.validation.support;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class IsUUIDValidator implements ConstraintValidator<IsUUID, String> {
    private static final Pattern UUID = Pattern.compile("^[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}$", 2);

    public IsUUIDValidator() {
    }

    @Override
    public boolean isValid(final String uuid, final ConstraintValidatorContext context) {
        return uuid == null || this.UUID.matcher(uuid).matches();
    }
}
