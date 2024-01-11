package be.ylorth.cibiouxrest.bl.servicesImpl;

import be.ylorth.cibiouxrest.bl.exception.DatePriseException;
import be.ylorth.cibiouxrest.bl.models.Calendrier;
import be.ylorth.cibiouxrest.dal.models.FermetureEntity;
import be.ylorth.cibiouxrest.dal.models.ReservationEntity;
import be.ylorth.cibiouxrest.dal.models.ReservationStatus;
import be.ylorth.cibiouxrest.dal.repositories.FermetureRepository;
import be.ylorth.cibiouxrest.dal.repositories.ReservationRepository;


import be.ylorth.cibiouxrest.pl.models.reservation.ReservationForm;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {
    @Mock
    private FermetureRepository fermetureRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;
    @Test
    public void testDateNonDispo() {
        // Arrange
        FermetureEntity fermetureEntity = new FermetureEntity();
        fermetureEntity.setDateDeFermeture(LocalDate.now().plusDays(3));
        
        ReservationEntity acceptedReservation = new ReservationEntity();
        acceptedReservation.setStatus(ReservationStatus.ACCEPTE);
        acceptedReservation.setPremierJour(LocalDate.now().plusWeeks(1));
        acceptedReservation.setDernierJour(LocalDate.now().plusWeeks(1).plusDays(1));
        
        List<FermetureEntity> fermetures = List.of(fermetureEntity);
        List<ReservationEntity> reservations = List.of(acceptedReservation);
        
        when(fermetureRepository.findAll()).thenReturn(fermetures);
        when(reservationRepository.findAll()).thenReturn(reservations);
        
        // Act
        Calendrier calendrier = reservationService.dateNonDispo();
        
        // Assert
        LocalDate fermetureDate = fermetureEntity.getDateDeFermeture();
        Set<LocalDate> reservationDates = Stream.iterate(acceptedReservation.getPremierJour(), date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(acceptedReservation.getPremierJour(), acceptedReservation.getDernierJour()))
                .collect(Collectors.toSet());

        System.out.println("fermetureDate: " + fermetureDate); 
        System.out.println("reservationDates: " + reservationDates); 
        System.out.println("calendrier.datesFermetures(): " + calendrier.datesFermetures()); 
        System.out.println("calendrier.datesReservees(): " + calendrier.datesReservees());
        
        assertTrue(calendrier.datesFermetures().contains(fermetureDate));
        assertTrue(calendrier.datesReservees().containsAll(reservationDates));
    }

    @Test
    void testGetReservationWhenOk() {
        // Arrange
        Long id = 1L;
        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setId(id);
        when(reservationRepository.findOne(any(Specification.class))).thenReturn(Optional.of(reservationEntity));

        // Act
        Optional<ReservationEntity> optionalReservationEntity = reservationService.getReservation(id);

        // Assert
        assertTrue(optionalReservationEntity.isPresent());
        assertEquals(id, optionalReservationEntity.get().getId());
    }

    @Test
    void testGetReservationWhenIdKO() {
        // Arrange
        Long id = -11L;
        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setId(id);
        when(reservationRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        // Act
        Optional<ReservationEntity> optionalReservationEntity = reservationService.getReservation(id);

        // Assert
        assertTrue(optionalReservationEntity.isEmpty());
    }

    @Test
    void testGetOneByDate() {
        // Arrange
        LocalDate testDate = LocalDate.now();
        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setPremierJour(testDate.minusDays(1));
        reservationEntity.setDernierJour(testDate.plusDays(1));
        when(reservationRepository.findOne(any(Specification.class))).thenReturn(Optional.of(reservationEntity));

        // Act
        Optional<ReservationEntity> optionalReservationEntity = reservationService.getOneByDate(testDate);

        // Assert
        assertTrue(optionalReservationEntity.isPresent());
        assertTrue(testDate.isAfter(optionalReservationEntity.get().getPremierJour())
                && testDate.isBefore(optionalReservationEntity.get().getDernierJour()));
    }

    @Test
    void testGetOneByDateWhenDateIsNull() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()->reservationService.getOneByDate(null));
        assertEquals("La date ne peut être null",exception.getMessage());
    }

    @Test
    void testAddReservationVisitor_WithFormNull() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.addReservationVisitor(null);
        });

        assertEquals("form can't be null", exception.getMessage());
    }
}
