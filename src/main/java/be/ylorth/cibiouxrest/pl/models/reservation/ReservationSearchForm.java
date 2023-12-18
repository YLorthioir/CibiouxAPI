package be.ylorth.cibiouxrest.pl.models.reservation;

import java.time.LocalDate;

public record ReservationSearchForm(
        String nom,
        String prenom,
        LocalDate dateReservationEntree,
        LocalDate dateReservationSortie,
        String email,
        String telephone
) {
}
