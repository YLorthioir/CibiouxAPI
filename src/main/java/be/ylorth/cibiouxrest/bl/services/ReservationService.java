package be.ylorth.cibiouxrest.bl.services;

import be.ylorth.cibiouxrest.dal.models.ReservationEntity;
import be.ylorth.cibiouxrest.bl.models.Calendrier;
import be.ylorth.cibiouxrest.dal.models.ReservationStatus;
import be.ylorth.cibiouxrest.pl.models.reservation.ReservationForm;
import be.ylorth.cibiouxrest.pl.models.reservation.ReservationSearchForm;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ReservationService {
    Calendrier dateNonDispo();
    Optional<ReservationEntity> getReservation(Long id);
    Set<ReservationEntity>  getReservationSemaine(LocalDate lundi, LocalDate Dimanche);
    void addReservationVisitor(ReservationForm form);
    void addReservationDirection(ReservationForm form);
    void changeReservationStatus(Long id, ReservationStatus status);
    void updateReservation(Long id, ReservationForm form);
    void deleteReservation(Long id);
    List<ReservationEntity> search(ReservationSearchForm form);
    
}
