package be.ylorth.cibiouxrest.bl;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        fermetureService = new FermetureServiceImpl(fermetureRepository);
    }

    @Test
    void testGetAll() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<FermetureEntity> expectedPage = new PageImpl<>(List.of(new FermetureEntity()));
        when(fermetureRepository.findAll(any(Pageable.class))).thenReturn(expectedPage);

        Page<FermetureEntity> actualPage = fermetureService.getAll(pageable);

        assertEquals(expectedPage, actualPage);
    }
    
}

