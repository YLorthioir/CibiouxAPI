package be.ylorth.cibiouxrest.dal.models;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.HashMap;

@Entity
@Table(name = "Reservation")
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
    private short nbPersonne;
    private String commentaire;
    @ElementCollection
    @CollectionTable(name = "repas")
    @MapKeyColumn(name = "date")
    @Column(name = "repas")
    private HashMap<LocalDate, Boolean> repas;
    
}
