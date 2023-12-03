package be.ylorth.cibiouxrest.bl.services;

import be.ylorth.cibiouxrest.dal.models.FermetureEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Set;

public interface FermetureService {
    Page<FermetureEntity> getAll(Pageable pageable);
    void create(Set<LocalDate> dates);
    void delete(LocalDate date);
    
}
