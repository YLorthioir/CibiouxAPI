package be.ylorth.cibiouxrest.pl.validators;

import be.ylorth.cibiouxrest.pl.models.reservation.ReservationForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateOrderValidator implements ConstraintValidator<ValidDateOrder, ReservationForm> {

    @Override
    public void initialize(final ValidDateOrder constraintAnnotation) {
    }

    @Override
    public boolean isValid(final ReservationForm form, final ConstraintValidatorContext context) {
        return form.dateReservationSortie().isAfter(form.dateReservationEntree());
    }
}