package be.ylorth.cibiouxrest.bl.servicesImpl;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import be.ylorth.cibiouxrest.bl.models.Calendrier;
import be.ylorth.cibiouxrest.bl.services.ReservationService;
import be.ylorth.cibiouxrest.bl.servicesImpl.FermetureServiceImpl;
import be.ylorth.cibiouxrest.bl.servicesImpl.ReservationServiceImpl;
import be.ylorth.cibiouxrest.dal.models.FermetureEntity;
import be.ylorth.cibiouxrest.dal.repositories.FermetureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.*;

class FermetureServiceTest {

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Mock
    private FermetureRepository fermetureRepository;

    @Mock
    private ReservationServiceImpl reservationService;

    @InjectMocks
    private FermetureServiceImpl fermetureService;

    @Test
    void testCreate() {

        Set<LocalDate> dates = Set.of(LocalDate.now());
        Calendrier calendrier = new Calendrier(new HashSet<>(),new HashSet<>());
        Mockito.when(reservationService.dateNonDispo()).thenReturn(calendrier);


        fermetureService.create(dates);


        Mockito.verify(fermetureRepository, Mockito.times(1)).save(Mockito.any(FermetureEntity.class));
    }

    @Test
    void testCreateWhenDateIsReserved() {

        LocalDate reservedDate = LocalDate.now().plusDays(2);
        Set<LocalDate> dates = Set.of(reservedDate);
        Calendrier calendrier = new Calendrier(dates,new HashSet<>());
        Mockito.when(reservationService.dateNonDispo()).thenReturn(calendrier);


        fermetureService.create(dates);


        Mockito.verify(fermetureRepository, Mockito.never()).save(Mockito.any(FermetureEntity.class));
    }

    @Test
    void testCreateWhenDateIsNull() {
        
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()->fermetureService.create(null));
        assertEquals("Les dates ne peuvent être nulles",exception.getMessage());
    }

    @Test
    void testCreateWhenSetIsEmpty() {

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()->fermetureService.create(new HashSet<>()));
        assertEquals("Les dates ne peuvent être nulles",exception.getMessage());
    }

    @Test
    void testCreateWhenDateInThePast() {
        Set<LocalDate> set = new HashSet<>();
        set.add(LocalDate.now().minusDays(2));
        set.add(LocalDate.now().minusDays(1));
        set.add(LocalDate.now());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()->fermetureService.create(set));
        assertEquals("Les dates ne peuvent être dans le passés",exception.getMessage());
    }
    
    @Test
    void testDeleteWhenEntityExists() {
        LocalDate date = LocalDate.now();
        FermetureEntity entity = new FermetureEntity();
        entity.setDateDeFermeture(date);
        Mockito.when(fermetureRepository.findOne(any(Specification.class))).thenReturn(Optional.of(entity));

        fermetureService.delete(date);

        verify(fermetureRepository, times(1)).delete(entity);
    }

    @Test
    void testDeleteWhenEntityDoesNotExist() {
        LocalDate date = LocalDate.now();
        FermetureEntity entity = new FermetureEntity();
        Mockito.when(fermetureRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        fermetureService.delete(date);

        verify(fermetureRepository, times(0)).delete(entity);
    }
    
}

