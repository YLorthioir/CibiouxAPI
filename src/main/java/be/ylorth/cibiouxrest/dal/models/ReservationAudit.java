package be.ylorth.cibiouxrest.dal.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class ReservationAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long reservationId;
    private String action;
    private String nomChamp;
    private String ancienneValeur;
    private String nouvelleValeur;
    private LocalDateTime heureDeModification;
}
