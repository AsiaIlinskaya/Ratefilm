package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ValidReleaseDate, LocalDate> {
    private String annotationDateAfter;

    @Override
    public void initialize(ValidReleaseDate date) {
        this.annotationDateAfter = date.value();
    }

    @Override
    public boolean isValid(LocalDate target, ConstraintValidatorContext context) {
        if (target != null) {
            return target.isAfter(LocalDate.parse(annotationDateAfter).minusDays(1));
        }
        return false;
    }
}