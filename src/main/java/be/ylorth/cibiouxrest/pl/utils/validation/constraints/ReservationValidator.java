package be.ylorth.cibiouxrest.pl.utils.validation.constraints;

import be.ylorth.cibiouxrest.pl.utils.validation.validators.CreneauReservationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CreneauReservationValidator.class)
public @interface ReservationValidator {

    String message() default "La date de départ doit être après celle d'arrivée";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

}
