package be.ylorth.cibiouxrest.pl.utils.validation.constraints;

import be.ylorth.cibiouxrest.pl.utils.validation.validators.DateOrderValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateOrderValidator.class)
@Documented
public @interface ValidDateOrder {

    String message() default "La date de sortie doit être après celle d'arrivée";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
