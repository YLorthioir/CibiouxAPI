package be.ylorth.cibiouxrest.dal.repositories;

import be.ylorth.cibiouxrest.dal.models.FermetureEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;

public interface FermetureRepository extends JpaRepository<FermetureEntity, Long>, JpaSpecificationExecutor<FermetureEntity> {
}
