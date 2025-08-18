package ru.concordia.cppd_service.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {
    private Class<? extends Enum<?>> enumClass;
    private boolean ignoreCase;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
        this.ignoreCase = constraintAnnotation.ignoreCase();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null)
            return true;

        return Arrays.stream(enumClass.getEnumConstants())
                .anyMatch(e -> {
                    String enumName = e.name();
                    return ignoreCase ? enumName.equalsIgnoreCase(value) : enumName.equals(value);
                });
    }
}

