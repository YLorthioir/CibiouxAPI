package be.ylorth.cibiouxrest.pl.utils.validation.validators;

import be.ylorth.cibiouxrest.pl.models.reservation.ReservationForm;
import be.ylorth.cibiouxrest.pl.utils.validation.constraints.ValidMealMap;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

public class MealMapValidator implements ConstraintValidator<ValidMealMap, ReservationForm> {

    @Override
    public boolean isValid(ReservationForm value, ConstraintValidatorContext context) {
        LocalDate premierJour = value.premierJour();
        LocalDate dernierJour = value.dernierJour();
        HashMap<LocalDate, Boolean> repas = value.repas();
        
        long daysBetween = ChronoUnit.DAYS.between(premierJour, dernierJour) + 1;
        
        if (repas.size() != daysBetween) {
            return false;
        }

        for (LocalDate date : repas.keySet()) {
            if (date.isBefore(premierJour) || date.isAfter(dernierJour)) {
                return false;
            }
        }
        
        return true;
    }
}
