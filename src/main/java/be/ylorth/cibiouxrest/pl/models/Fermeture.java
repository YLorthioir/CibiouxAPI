package be.ylorth.cibiouxrest.pl.models;

import be.ylorth.cibiouxrest.dal.models.FermetureEntity;

import java.time.LocalDate;

public record Fermeture(Long id, LocalDate dateDeFermeture) {
    public static Fermeture fromEntity(FermetureEntity entity){
        return new Fermeture(entity.getId(), entity.getDateDeFermeture());
    }
}
