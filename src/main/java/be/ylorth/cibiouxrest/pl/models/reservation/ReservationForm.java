package be.ylorth.cibiouxrest.pl.models.reservation;

import be.ylorth.cibiouxrest.dal.models.ReservationEntity;
import be.ylorth.cibiouxrest.pl.utils.validation.constraints.ValidDateOrder;
import be.ylorth.cibiouxrest.pl.utils.validation.constraints.ValidMealMap;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.HashMap;

@ValidDateOrder
@ValidMealMap
public record ReservationForm(
        @NotBlank String nom,
        @NotBlank String prenom,
        @FutureOrPresent LocalDate premierJour,
        @FutureOrPresent LocalDate dernierJour,
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
        entity.setDernierJour(form.dernierJour());
        entity.setPremierJour(form.premierJour());
        entity.setRepas(form.repas());
        return entity;
    }
}
