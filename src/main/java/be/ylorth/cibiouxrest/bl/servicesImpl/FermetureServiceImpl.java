package be.ylorth.cibiouxrest.bl.servicesImpl;

import be.ylorth.cibiouxrest.bl.services.FermetureService;
import be.ylorth.cibiouxrest.dal.models.FermetureEntity;
import be.ylorth.cibiouxrest.dal.repositories.FermetureRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Set;

@Service
public class FermetureServiceImpl implements FermetureService{

    private final FermetureRepository fermetureRepository;

    public FermetureServiceImpl(FermetureRepository fermetureRepository) {
        this.fermetureRepository = fermetureRepository;
    }

    @Override
    public Page<FermetureEntity> getAll(Pageable pageable) {
        Sort sort = Sort.by("dateDeFermeture").ascending();
        Pageable page = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return fermetureRepository.findAll(page);
    }

    @Override
    public void create(Set<LocalDate> dates) {
        dates.stream()
                .filter(this::doesNotExist)
                .map(this::dateToFermetureEntity)
                .forEach(fermetureRepository::save);
    }

    @Override
    public void delete(LocalDate date) {
        fermetureRepository.findOne(byDate(date))
                .ifPresent(fermetureRepository::delete);
    }

    private FermetureEntity dateToFermetureEntity(LocalDate date) {
        FermetureEntity entity = new FermetureEntity();
        entity.setDateDeFermeture(date);
        return entity;
    }

    private boolean doesNotExist(LocalDate date) {
        return !fermetureRepository.exists(byDate(date));
    }

    private Specification<FermetureEntity> byDate(LocalDate date){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("dateDeFermeture"),date);
    }
}
