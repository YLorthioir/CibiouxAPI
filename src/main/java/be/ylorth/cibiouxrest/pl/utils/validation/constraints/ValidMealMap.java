package be.ylorth.cibiouxrest.pl.utils.validation.constraints;

import be.ylorth.cibiouxrest.pl.utils.validation.validators.MealMapValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MealMapValidator.class) // Le validateur que nous créerons à l'étape suivante
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMealMap {
    String message() default "Repas invalides";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}