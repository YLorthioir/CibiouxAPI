package be.ylorth.cibiouxrest.pl.models.reservation;

import java.time.LocalDate;
import java.util.HashMap;

public record Reservation(
        Long id,
        String nom,
        String prenom,
        LocalDate dateReservationEntree,
        LocalDate dateReservationSortie,
        String email,
        String telephone,
        short nbPersonne,
        String commentaire,
        HashMap<LocalDate, Boolean> repas
) {
}
