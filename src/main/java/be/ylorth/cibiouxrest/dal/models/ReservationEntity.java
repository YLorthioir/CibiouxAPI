package be.ylorth.cibiouxrest.dal.models;

import be.ylorth.cibiouxrest.dal.utils.ReservationAuditListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@EntityListeners(ReservationAuditListener.class)
@Table(name = "Reservation")
@Getter @Setter
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String nom;
    @Column(nullable = false)
    private String prenom;
    @Column(nullable = false)
    @Getter
    private LocalDate dateReservationEntree;
    @Column(nullable = false)
    @Getter
    private LocalDate dateReservationSortie;
    private String email;
    @Column(nullable = false)
    private String telephone;
    @Column(nullable = false)
    private int nbPersonne;
    private String commentaire;
    @ElementCollection
    @CollectionTable(name = "repas")
    @MapKeyColumn(name = "date")
    @Column(name = "repas")
    private Map<LocalDate, Boolean> repas;
    
}
