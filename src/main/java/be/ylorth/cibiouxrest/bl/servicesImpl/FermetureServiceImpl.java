package be.ylorth.cibiouxrest.bl.servicesImpl;

import be.ylorth.cibiouxrest.bl.models.Calendrier;
import be.ylorth.cibiouxrest.bl.services.FermetureService;
import be.ylorth.cibiouxrest.bl.services.ReservationService;
import be.ylorth.cibiouxrest.dal.models.FermetureEntity;
import be.ylorth.cibiouxrest.dal.repositories.FermetureRepository;
import org.hibernate.cache.spi.support.NaturalIdReadOnlyAccess;
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
    private final ReservationService reservationService;

    public FermetureServiceImpl(FermetureRepository fermetureRepository, ReservationService reservationService) {
        this.fermetureRepository = fermetureRepository;
        this.reservationService = reservationService;
    }

    @Override
    public void create(Set<LocalDate> dates) {
        if(dates == null || dates.isEmpty())
            throw new IllegalArgumentException("Les dates ne peuvent être nulles");

        if(dates.stream().anyMatch(date->date.isBefore(LocalDate.now())))
            throw new IllegalArgumentException("Les dates ne peuvent être dans le passés");
        
        dates.stream()
                .filter(this::dateFermetureValide)
                .map(this::dateToFermetureEntity)
                .forEach(fermetureRepository::save);
    }

    @Override
    public void delete(LocalDate date) {
        fermetureRepository.findOne(byDate(date))
                .ifPresent(fermetureRepository::delete);
    }
    
    private boolean dateFermetureValide(LocalDate date){
        Calendrier datesNonDispo = reservationService.dateNonDispo();
        return !datesNonDispo.datesReservees().contains(date) && !datesNonDispo.datesFermetures().contains(date);
    }

    private FermetureEntity dateToFermetureEntity(LocalDate date) {
        FermetureEntity entity = new FermetureEntity();
        entity.setDateDeFermeture(date);
        return entity;
    }

    private Specification<FermetureEntity> byDate(LocalDate date){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("dateDeFermeture"),date);
    }
}
