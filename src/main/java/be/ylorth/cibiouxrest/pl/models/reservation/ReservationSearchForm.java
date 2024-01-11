package be.ylorth.cibiouxrest.pl.models.reservation;

import java.time.LocalDate;

public record ReservationSearchForm(
        String nom,
        String prenom,
        LocalDate premierJour,
        LocalDate dernierJour,
        String email,
        String telephone
) {
}
