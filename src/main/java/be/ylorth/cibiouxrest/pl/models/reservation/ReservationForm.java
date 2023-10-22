package be.ylorth.cibiouxrest.pl.models.reservation;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.HashMap;

public record ReservationForm(
        @NotBlank String nom,
        @NotBlank String prenom,
        @FutureOrPresent LocalDate dateReservationEntree,
        @Future LocalDate dateReservationSortie,
        String email,
        @NotBlank @Size(max = 15) String telephone,
        @Max(value = 3) @Min(value = 1) short nbPersonne,
        @Size(max = 250) String commentaire,
        HashMap<LocalDate, Boolean> repas
        
) {
}
