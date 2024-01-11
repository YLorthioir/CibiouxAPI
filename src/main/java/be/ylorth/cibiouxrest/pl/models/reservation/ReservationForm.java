package be.ylorth.cibiouxrest.pl.models.reservation;

import be.ylorth.cibiouxrest.dal.models.ReservationEntity;
import be.ylorth.cibiouxrest.pl.validators.ValidDateOrder;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.HashMap;

@ValidDateOrder
public record ReservationForm(
        @NotBlank String nom,
        @NotBlank String prenom,
        @FutureOrPresent LocalDate dateReservationEntree,
        @Future LocalDate dateReservationSortie,
        @Email
        String email,
        @NotBlank @Size(max = 15) String telephone,
        @Max(value = 4) @Min(value = 1) int nbPersonne,
        @Size(max = 250) String commentaire,
        @Size(min = 1)
        HashMap<LocalDate, Boolean> repas
        
) {
    public static ReservationEntity toEntity(ReservationForm form){
        ReservationEntity entity = new ReservationEntity();
        entity.setNom(form.nom());
        entity.setPrenom(form.prenom());
        entity.setEmail(form.email());
        entity.setNbPersonne(form.nbPersonne());
        entity.setTelephone(form.telephone());
        entity.setCommentaire(form.commentaire());
        entity.setDateReservationDernierJour(form.dateReservationSortie().minusDays(1));
        entity.setDateReservationPremierJour(form.dateReservationEntree());
        entity.setRepas(form.repas());
        return entity;
    }
}
