package be.ylorth.cibiouxrest.pl.utils.validation.validators;

import be.ylorth.cibiouxrest.pl.utils.validation.constraints.ReservationValidator;
import be.ylorth.cibiouxrest.pl.models.reservation.ReservationForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class CreneauReservationValidator implements ConstraintValidator<ReservationValidator, ReservationForm> {

    @Override
    public boolean isValid(ReservationForm value, ConstraintValidatorContext context) {
        return value.dateReservationEntree().isBefore(value.dateReservationSortie());
    }


}
