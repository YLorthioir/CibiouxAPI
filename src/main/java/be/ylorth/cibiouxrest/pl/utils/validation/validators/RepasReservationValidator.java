package be.ylorth.cibiouxrest.pl.utils.validation.validators;

import be.ylorth.cibiouxrest.pl.models.reservation.ReservationForm;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.temporal.ChronoUnit;


public class RepasReservationValidator implements ConstraintValidator<be.ylorth.cibiouxrest.pl.utils.validation.constraints.ReservationValidator, ReservationForm> {

    @Override
    public boolean isValid(ReservationForm value, ConstraintValidatorContext context) {
        return value.repas().size() <= ChronoUnit.DAYS.between(value.dateReservationSortie(), value.dateReservationEntree());
    }
}