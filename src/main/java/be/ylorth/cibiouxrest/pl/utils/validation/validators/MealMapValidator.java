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
    public boolean isValid(ReservationForm form, ConstraintValidatorContext context) {
        if(form.repas() == null)
            throw new IllegalArgumentException("Les repas ne peuvent pas être null");

        if(form.premierJour() == null || form.dernierJour() == null)
            throw new IllegalArgumentException("Le premier jour ou le dernier jour ne peuvent pas être null");
        
        LocalDate premierJour = form.premierJour();
        LocalDate dernierJour = form.dernierJour();
        HashMap<LocalDate, Boolean> repas = form.repas();
        
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
