package be.ylorth.cibiouxrest.bl.servicesImpl;

import be.ylorth.cibiouxrest.bl.models.Calendrier;
import be.ylorth.cibiouxrest.bl.services.ReservationService;
import be.ylorth.cibiouxrest.dal.models.FermetureEntity;
import be.ylorth.cibiouxrest.dal.models.ReservationEntity;
import be.ylorth.cibiouxrest.dal.repositories.FermetureRepository;
import be.ylorth.cibiouxrest.dal.repositories.ReservationRepository;
import be.ylorth.cibiouxrest.pl.models.reservation.Reservation;
import be.ylorth.cibiouxrest.pl.models.reservation.ReservationForm;
import org.springframework.stereotype.Service;
import org.springframework.validation.DataBinder;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReservationServiceImpl implements ReservationService {
    
    private final FermetureRepository fermetureRepository;
    private final ReservationRepository reservationRepository;

    public ReservationServiceImpl(FermetureRepository fermetureRepository, ReservationRepository reservationRepository) {
        this.fermetureRepository = fermetureRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public Calendrier dateNonDispo() {
        Set<LocalDate> fermeture = fermetureRepository.findAll().stream()
                .map(FermetureEntity::getDateDeFermeture)
                .filter(dateDeFermeture -> dateDeFermeture.isAfter(LocalDate.now()))
                .collect(Collectors.toSet());

        Set<LocalDate> reserve = reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getDateReservationEntree().isBefore(LocalDate.now().minusDays(1)))
                .flatMap(reservation -> {
                    LocalDate dateEntree = reservation.getDateReservationEntree();
                    LocalDate dateSortie = reservation.getDateReservationSortie();
                    return Stream.iterate(dateEntree, date -> date.plusDays(1))
                            .limit(ChronoUnit.DAYS.between(dateEntree, dateSortie.plusDays(1)));
                })
                .collect(Collectors.toSet());
        
        return new Calendrier(reserve, fermeture);
    }

    @Override
    public Optional<ReservationEntity> getReservation(Long id) {
        return Optional.empty();
    }

    @Override
    public Set<ReservationEntity> getReservationSemaine(LocalDate lundi, LocalDate Dimanche) {
        return null;
    }

    @Override
    public void addReservation(ReservationForm form) {

    }

    @Override
    public void updateReservation(Long id, ReservationForm form) {

    }

    @Override
    public void deleteReservation(Long id) {

    }
}
