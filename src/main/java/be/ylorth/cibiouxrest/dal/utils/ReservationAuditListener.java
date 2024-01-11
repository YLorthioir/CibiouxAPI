package be.ylorth.cibiouxrest.dal.utils;

import be.ylorth.cibiouxrest.dal.models.ReservationAudit;
import be.ylorth.cibiouxrest.dal.models.ReservationEntity;
import be.ylorth.cibiouxrest.dal.repositories.AuditRepository;
import be.ylorth.cibiouxrest.dal.repositories.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ReservationAuditListener{
    
    @Autowired @Lazy
    private AuditRepository auditRepository;
    @Autowired @Lazy
    private ReservationRepository reservationRepository;

    @PrePersist
    public void persistAudit(ReservationEntity reservation) {
        saveAudit(reservation, "CREATE");
    }

    @PreRemove
    public void removeAudit(ReservationEntity reservation) {
        saveAudit(reservation, "DELETE");
    }

    @PreUpdate
    public void updateAudit(ReservationEntity reservation) {
        ReservationAudit auditEntry = new ReservationAudit();
        auditEntry.setHeureDeModification(LocalDateTime.now());

        ReservationEntity initialState = reservationRepository.findById(reservation.getId()).get();

        auditEntry.setAction("UPDATE");
        List<String> modifiedFields = getModifiedFields(initialState, reservation);
        auditEntry.setNomChamp(modifiedFields.toString());
        auditEntry.setAncienneValeur(initialState.toString());
        auditEntry.setNouvelleValeur(reservation.toString());

        auditRepository.save(auditEntry);
    }

    private void saveAudit(ReservationEntity reservation, String action) {
        ReservationAudit auditEntry = new ReservationAudit();
        auditEntry.setReservationId(reservation.getId());;
        auditEntry.setHeureDeModification(LocalDateTime.now());
        auditEntry.setAction(action);
        auditRepository.save(auditEntry);
    }

    private List<String> getModifiedFields(ReservationEntity initialState, ReservationEntity newState) {
        List<String> modifiedFields = new ArrayList<>();

        if (!Objects.equals(initialState.getNom(), newState.getNom())) {
            modifiedFields.add("nom");
        }
        if (!Objects.equals(initialState.getPrenom(), newState.getPrenom())) {
            modifiedFields.add("prenom");
        }
        if (!Objects.equals(initialState.getDateReservationPremierJour(), newState.getDateReservationPremierJour())) {
            modifiedFields.add("dateReservationEntree");
        }
        if (!Objects.equals(initialState.getDateReservationDernierJour(), newState.getDateReservationDernierJour())) {
            modifiedFields.add("dateReservationSortie");
        }
        if (!Objects.equals(initialState.getEmail(), newState.getEmail())) {
            modifiedFields.add("email");
        }
        if (!Objects.equals(initialState.getTelephone(), newState.getTelephone())) {
            modifiedFields.add("telephone");
        }
        if (initialState.getNbPersonne() != newState.getNbPersonne()) {
            modifiedFields.add("nbPersonne");
        }
        if (!Objects.equals(initialState.getCommentaire(), newState.getCommentaire())) {
            modifiedFields.add("commentaire");
        }
        if (!Objects.equals(initialState.getRepas(), newState.getRepas())) {
            modifiedFields.add("repas");
        }
        return modifiedFields;
    }
}