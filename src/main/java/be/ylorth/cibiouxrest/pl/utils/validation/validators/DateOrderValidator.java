package be.ylorth.cibiouxrest.pl.utils.validation.validators;

import be.ylorth.cibiouxrest.pl.models.reservation.ReservationForm;
import be.ylorth.cibiouxrest.pl.utils.validation.constraints.ValidDateOrder;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateOrderValidator implements ConstraintValidator<ValidDateOrder, ReservationForm> {

    @Override
    public void initialize(final ValidDateOrder constraintAnnotation) {
    }

    @Override
    public boolean isValid(final ReservationForm form, final ConstraintValidatorContext context) {
        if(form.premierJour() == null || form.dernierJour() == null)
            throw new IllegalArgumentException("Le premier jour ou le dernier jour ne peuvent pas être null");
        return form.dernierJour().isAfter(form.premierJour().minusDays(1));
    }
}