package be.ylorth.cibiouxrest.bl;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import be.ylorth.cibiouxrest.bl.services.ReservationService;
import be.ylorth.cibiouxrest.bl.servicesImpl.FermetureServiceImpl;
import be.ylorth.cibiouxrest.dal.models.FermetureEntity;
import be.ylorth.cibiouxrest.dal.repositories.FermetureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

class FermetureServiceTest {
    
    FermetureServiceImpl fermetureService;

    @Mock
    FermetureRepository fermetureRepository;

    @Mock
    ReservationService reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        fermetureService = new FermetureServiceImpl(fermetureRepository, reservationService);
    }
    
    
}

