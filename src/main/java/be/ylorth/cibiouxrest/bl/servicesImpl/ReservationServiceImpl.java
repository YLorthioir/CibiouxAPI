package be.ylorth.cibiouxrest.bl.servicesImpl;

import be.ylorth.cibiouxrest.bl.exception.DatePriseException;
import be.ylorth.cibiouxrest.bl.models.Calendrier;
import be.ylorth.cibiouxrest.bl.services.ReservationService;
import be.ylorth.cibiouxrest.dal.models.FermetureEntity;
import be.ylorth.cibiouxrest.dal.models.ReservationEntity;
import be.ylorth.cibiouxrest.dal.models.ReservationStatus;
import be.ylorth.cibiouxrest.dal.repositories.FermetureRepository;
import be.ylorth.cibiouxrest.dal.repositories.ReservationRepository;
import be.ylorth.cibiouxrest.pl.models.reservation.ReservationForm;
import be.ylorth.cibiouxrest.pl.models.reservation.ReservationSearchForm;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReservationServiceImpl implements ReservationService {
    
    private final FermetureRepository fermetureRepository;
    private final ReservationRepository reservationRepository;
    private final MailServiceImpl mailService;

    public ReservationServiceImpl(FermetureRepository fermetureRepository, ReservationRepository reservationRepository, MailServiceImpl mailService) {
        this.fermetureRepository = fermetureRepository;
        this.reservationRepository = reservationRepository;
        this.mailService = mailService;
    }

    @Override
    public Calendrier dateNonDispo() {
        Set<LocalDate> fermeture = fermetureRepository.findAll().stream()
                .map(FermetureEntity::getDateDeFermeture)
                .filter(dateDeFermeture -> dateDeFermeture.isAfter(LocalDate.now()))
                .collect(Collectors.toSet());
        
        Set<LocalDate> reserve = reservationRepository.findAll().stream()
                .filter(reservation -> reservation.getStatus() == ReservationStatus.ACCEPTE)
                .filter(reservation -> reservation.getDateReservationEntree().isAfter(LocalDate.now().minusDays(1)) || reservation.getDateReservationSortie().isAfter(LocalDate.now()))
                .flatMap(reservation -> {
                    LocalDate dateEntree = reservation.getDateReservationEntree();
                    LocalDate dateSortie = reservation.getDateReservationSortie();
                    return Stream.iterate(dateEntree, date -> date.plusDays(1))
                            .limit(ChronoUnit.DAYS.between(dateEntree, dateSortie));
                })
                .filter(d -> d.isAfter(LocalDate.now().minusDays(1)))
                .collect(Collectors.toSet());
        
        return new Calendrier(reserve, fermeture);
    }

    @Override
    public Optional<ReservationEntity> getReservation(Long id) {
        Specification<ReservationEntity> specification = (((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"),id)));
        return reservationRepository.findOne(specification);
    }

    @Override
    public Optional<ReservationEntity> getOneByDate(LocalDate date) {
        Specification<ReservationEntity> specification = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.lessThanOrEqualTo(root.get("dateReservationEntree"),date),
                criteriaBuilder.greaterThanOrEqualTo(root.get("dateReservationSortie"),date));

        return reservationRepository.findOne(specification);
    }

    @Override
    public void addReservationVisitor(ReservationForm form) {
        if(form==null)
            throw new IllegalArgumentException("form can't be null");

        LocalDate start = form.dateReservationEntree();
        LocalDate end = form.dateReservationSortie();

        boolean allDatesAvailable = start.datesUntil(end)
                .allMatch(this::checkDateAvailable);

        if(!allDatesAvailable) {
            throw new DatePriseException("One or more dates in the range are not available for reservation");
        }
        
        mailService.sendReservationMessage(form.email(), "reservation added", "ceci est un test");
        ReservationEntity entity = ReservationForm.toEntity(form);
        entity.setStatus(ReservationStatus.EN_ATTENTE);
        reservationRepository.save(entity);
    }

    @Override
    public void addReservationDirection(ReservationForm form) {
        if(form==null)
            throw new IllegalArgumentException("form can't be null");

        LocalDate start = form.dateReservationEntree();
        LocalDate end = form.dateReservationSortie();

        boolean allDatesAvailable = start.datesUntil(end)
                .allMatch(this::checkDateAvailable);

        if(!allDatesAvailable) {
            throw new IllegalArgumentException("One or more dates in the range are not available for reservation");
        }
        
        mailService.sendReservationMessage(form.email(), "reservation confirmed", "ceci est un test");
        ReservationEntity entity = ReservationForm.toEntity(form);
        entity.setStatus(ReservationStatus.ACCEPTE);
        reservationRepository.save(entity);
    }
    
    @Override
    public void changeReservationStatus(Long id, ReservationStatus status) {
        ReservationEntity entity = reservationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Reservation not found"));
        entity.setStatus(status);
        mailService.sendReservationMessage(entity.getEmail(), "reservation " + status.getStatus(), "ceci est un test");
        reservationRepository.save(entity);
    }

    @Override
    public void updateReservation(Long id, ReservationForm form) {
        if(form==null)
            throw new IllegalArgumentException("form can't be null");

        LocalDate start = form.dateReservationEntree();
        LocalDate end = form.dateReservationSortie();

        boolean allDatesAvailable = start.datesUntil(end)
                .allMatch(this::checkDateAvailable);

        if(!allDatesAvailable) {
            throw new IllegalArgumentException("One or more dates in the range are not available for reservation");
        }

        mailService.sendReservationMessage(form.email(), "reservation modified", "ceci est un test");
        
        ReservationEntity entity = reservationRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Reservation not found"));
        
        entity.setNom(form.nom());
        entity.setPrenom(form.prenom());
        entity.setCommentaire(form.commentaire());
        entity.setEmail(form.email());
        entity.setDateReservationEntree(form.dateReservationEntree());
        entity.setDateReservationSortie(form.dateReservationSortie());
        entity.setTelephone(form.telephone());
        entity.setNbPersonne(form.nbPersonne());
        entity.setRepas(new HashMap<>(form.repas()));
        
        reservationRepository.save(entity);
    }

    @Override
    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    @Override
    public List<ReservationEntity> search(ReservationSearchForm form) {
        return reservationRepository.findAll(specificationBuilder(form));
    }
    
    @Override
    public List<ReservationEntity> getPendings() {
        Specification<ReservationEntity> specification = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), ReservationStatus.EN_ATTENTE);
        return reservationRepository.findAll(specification);
    }
    
    private Specification<ReservationEntity> specificationBuilder(ReservationSearchForm form){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (form.nom() != null && !form.nom().isBlank())
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("nom")), "%" + form.nom().toLowerCase() + "%"));
            
            if (form.prenom() != null && !form.prenom().isBlank())
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("prenom")), "%" + form.prenom().toLowerCase() + "%"));
            
            if (form.dateReservationEntree() != null)
                predicates.add(criteriaBuilder.equal(root.get("dateReservationEntree"), form.dateReservationEntree()));
        
            if (form.dateReservationSortie() != null)
                predicates.add(criteriaBuilder.equal(root.get("dateReservationSortie"), form.dateReservationSortie()));
        
            if (form.email() != null && !form.email().isBlank())
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("email")), "%" + form.email().toLowerCase() + "%"));
        
            if (form.telephone() != null && !form.telephone().isBlank())
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("telephone")), "%" + form.telephone().toLowerCase() + "%"));
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private boolean checkDateAvailable(LocalDate date) {
        Calendrier calendrier = dateNonDispo();
        boolean dateIsReserved = calendrier.datesReservees().contains(date);
        boolean dateIsFermeture = calendrier.datesFermetures().contains(date);

        return !dateIsReserved && !dateIsFermeture;
    }
    
    
}
