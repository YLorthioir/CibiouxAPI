package be.ylorth.cibiouxrest.dal.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
public class FermetureEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Getter
    @Column(unique = true,nullable = false)
    private LocalDate dateDeFermeture;
}
