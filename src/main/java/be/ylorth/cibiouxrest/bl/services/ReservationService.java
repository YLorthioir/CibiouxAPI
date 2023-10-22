package be.ylorth.cibiouxrest.bl.services;

import be.ylorth.cibiouxrest.dal.models.ReservationEntity;
import be.ylorth.cibiouxrest.bl.models.Calendrier;
import be.ylorth.cibiouxrest.pl.models.reservation.ReservationForm;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

public interface ReservationService {
    Calendrier dateNonDispo();
    Optional<ReservationEntity> getReservation(Long id);
    Set<ReservationEntity>  getReservationSemaine(LocalDate lundi, LocalDate Dimanche);
    void addReservation(ReservationForm form);
    void updateReservation(Long id, ReservationForm form);
    void deleteReservation(Long id);
    
}
