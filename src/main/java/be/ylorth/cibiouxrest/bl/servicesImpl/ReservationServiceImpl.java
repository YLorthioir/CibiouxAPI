package be.ylorth.cibiouxrest.bl.servicesImpl;

import be.ylorth.cibiouxrest.bl.exception.NotFoundException;
import be.ylorth.cibiouxrest.bl.models.Calendrier;
import be.ylorth.cibiouxrest.bl.services.ReservationService;
import be.ylorth.cibiouxrest.dal.models.FermetureEntity;
import be.ylorth.cibiouxrest.dal.models.ReservationEntity;
import be.ylorth.cibiouxrest.dal.models.ReservationStatus;
import be.ylorth.cibiouxrest.dal.repositories.FermetureRepository;
import be.ylorth.cibiouxrest.dal.repositories.ReservationRepository;
import be.ylorth.cibiouxrest.pl.models.reservation.ReservationForm;
import be.ylorth.cibiouxrest.pl.models.reservation.ReservationSearchForm;
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
    public Set<ReservationEntity> getReservationSemaine(LocalDate lundi, LocalDate dimanche) {
        Specification<ReservationEntity> specification = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.or(
                        criteriaBuilder.and(
                                criteriaBuilder.greaterThanOrEqualTo(root.get("dateReservationEntree"), lundi),
                                criteriaBuilder.lessThanOrEqualTo(root.get("dateReservationEntree"), dimanche)
                        ),
                        criteriaBuilder.and(
                                criteriaBuilder.greaterThanOrEqualTo(root.get("dateReservationSortie"), lundi),
                                criteriaBuilder.lessThan(root.get("dateReservationSortie"), dimanche)
                        )
                ));

        return new HashSet<>(reservationRepository.findAll(specification));
    }

    @Override
    public void addReservationVisitor(ReservationForm form) {
        if(form==null)
            throw new IllegalArgumentException("form can't be null");
        mailService.sendReservationMessage(form.email(), "reservation added", "ceci est un test");
        ReservationEntity entity = ReservationForm.toEntity(form);
        entity.setStatus(ReservationStatus.EN_ATTENTE);
        reservationRepository.save(entity);
    }

    @Override
    public void addReservationDirection(ReservationForm form) {
        if(form==null)
            throw new IllegalArgumentException("form can't be null");
        mailService.sendReservationMessage(form.email(), "reservation confirmed", "ceci est un test");
        ReservationEntity entity = ReservationForm.toEntity(form);
        entity.setStatus(ReservationStatus.ACCEPTE);
        reservationRepository.save(entity);
    }
    
    @Override
    public void changeReservationStatus(Long id, ReservationStatus status) {
        ReservationEntity entity = reservationRepository.findById(id).orElseThrow(() -> new NotFoundException("Reservation not found"));
        entity.setStatus(status);
        mailService.sendReservationMessage(entity.getEmail(), "reservation " + status.getStatus(), "ceci est un test");
        reservationRepository.save(entity);
    }

    @Override
    public void updateReservation(Long id, ReservationForm form) {
        if(form==null)
            throw new IllegalArgumentException("form can't be null");
        
        ReservationEntity entity = reservationRepository.findById(id).orElseThrow(()->new NotFoundException("Reservation not found"));
        
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
    
    private Specification<ReservationEntity> specificationBuilder(ReservationSearchForm form){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (form.nom() != null || !form.nom().isBlank())
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("nom")), form.nom().toLowerCase()));
            
            if (form.prenom() != null || !form.prenom().isBlank())
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("prenom")), form.prenom().toLowerCase()));
            
            if (form.dateReservationEntree() != null)
                predicates.add(criteriaBuilder.equal(root.get("dateReservationEntree"), form.dateReservationEntree()));
        
            if (form.dateReservationSortie() != null)
                predicates.add(criteriaBuilder.equal(root.get("dateReservationSortie"), form.dateReservationSortie()));
        
            if (form.email() != null || !form.email().isBlank())
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("email")), form.email().toLowerCase()));
        
            if (form.telephone() != null || !form.telephone().isBlank())
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("telephone")), form.telephone().toLowerCase()));
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
