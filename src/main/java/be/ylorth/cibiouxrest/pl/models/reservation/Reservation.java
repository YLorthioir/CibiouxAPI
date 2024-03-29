package be.ylorth.cibiouxrest.pl.models.reservation;

import be.ylorth.cibiouxrest.dal.models.ReservationEntity;
import be.ylorth.cibiouxrest.dal.models.ReservationStatus;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.HashMap;

public record Reservation(
        Long id,
        String nom,
        String prenom,
        LocalDate premierJour,
        LocalDate dernierJour,
        String email,
        String telephone,
        int nbPersonne,
        String commentaire,
        HashMap<LocalDate, Boolean> repas,
        ReservationStatus status
) {
    public static Reservation fromEntity(ReservationEntity entity){
        return new Reservation(entity.getId(), entity.getNom(), entity.getPrenom(), entity.getPremierJour(), entity.getDernierJour(), entity.getEmail(), entity.getTelephone(), entity.getNbPersonne(), entity.getCommentaire(), new HashMap<>(entity.getRepas()),entity.getStatus());
    }
}
