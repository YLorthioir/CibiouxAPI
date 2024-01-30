package be.ylorth.cibiouxrest.dal.models;

import be.ylorth.cibiouxrest.dal.utils.ReservationAuditListener;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ReservationAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long reservationId;
    private String action;
    private String nomChamp;
    @Column(length = 1000)
    private String ancienneValeur;
    @Column(length = 1000)
    private String nouvelleValeur;
    private LocalDateTime heureDeModification;
    
}
