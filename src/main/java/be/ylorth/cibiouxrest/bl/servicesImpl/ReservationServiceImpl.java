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
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
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
                .filter(reservation -> reservation.getDernierJour().isAfter(LocalDate.now()))
                .flatMap(reservation -> {
                    LocalDate dateEntree = reservation.getPremierJour();
                    LocalDate dateSortie = reservation.getDernierJour().plusDays(1);
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
        if(date == null)
            throw new IllegalArgumentException("La date ne peut être null");
        
        Specification<ReservationEntity> specification = (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.lessThanOrEqualTo(root.get("premierJour"),date),
                criteriaBuilder.greaterThanOrEqualTo(root.get("dernierJour"),date));

        return reservationRepository.findOne(specification);
    }

    @Override
    public void addReservationVisitor(ReservationForm form) {
        if(form==null)
            throw new IllegalArgumentException("form can't be null");

        LocalDate start = form.premierJour();
        LocalDate end = form.dernierJour().plusDays(1);

        start.datesUntil(end).forEach(this::ensureDateIsAvailable);

        System.out.println(form.repas());
        ReservationEntity entity = ReservationForm.toEntity(form);

        try {
            mailService.sendReservationMessage(entity);
        }catch (MessagingException ex) {
            System.out.println("Erreur envoi mail: "+ex);
        }
        entity.setStatus(ReservationStatus.EN_ATTENTE);
        reservationRepository.save(entity);
    }

    @Override
    public void addReservationDirection(ReservationForm form) {
        if(form==null)
            throw new IllegalArgumentException("form can't be null");

        LocalDate start = form.premierJour();
        LocalDate end = form.dernierJour().plusDays(1);

        start.datesUntil(end).forEach(this::ensureDateIsAvailable);
        
        ReservationEntity entity = ReservationForm.toEntity(form);
        entity.setStatus(ReservationStatus.ACCEPTE);

        if(entity.getEmail()!=null && !entity.getEmail().isEmpty()) {
            try {
                mailService.sendAcceptedMessage(entity);
            } catch (MessagingException ex) {
                System.out.println("Erreur envoi mail: " + ex);
            }
        }
        
        reservationRepository.save(entity);
    }
    
    @Override
    public void changeReservationStatus(Long id, ReservationStatus status) {
        ReservationEntity entity = reservationRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée"));

        if (status == ReservationStatus.ACCEPTE)
            entity.getPremierJour().datesUntil(entity.getDernierJour().plusDays(1)).forEach(this::ensureDateIsAvailable);

        entity.setStatus(status);

        if(entity.getEmail()!=null && !entity.getEmail().isBlank()) {
            try {
                if (entity.getStatus() == ReservationStatus.ACCEPTE)
                    mailService.sendAcceptedMessage(entity);
                else if (entity.getStatus() == ReservationStatus.REFUSE)
                    mailService.sendDeniedMessage(entity);
            } catch (MessagingException ex) {
                System.out.println("Erreur envoi mail: " + ex);
            }
        }
        
        reservationRepository.save(entity);
    }

    @Override
    public void updateReservation(Long id, ReservationForm form, ReservationStatus status) {
        
        if(form==null)
            throw new IllegalArgumentException("form can't be null");

        if(status==null)
            throw new IllegalArgumentException("status can't be null");
        
        ReservationEntity entity = reservationRepository.findById(id).orElseThrow(()->new EntityNotFoundException("Réservation non trouvée"));

        boolean statutChanged = !((form.email() == null && entity.getEmail() == null) || form.email()!= null && form.email().equals(entity.getEmail()));
        
        entity.setNom(form.nom());
        entity.setPrenom(form.prenom());
        entity.setCommentaire(form.commentaire());
        if(entity.getEmail()!=null && form.email()==null)
            throw new IllegalArgumentException("L'email existant ne peut être supprimé");
        entity.setEmail(form.email());
        if(!(entity.getStatus()==ReservationStatus.ACCEPTE && (entity.getPremierJour().equals(form.premierJour()) && entity.getDernierJour().equals(form.dernierJour())))){
            form.premierJour().datesUntil(form.dernierJour().plusDays(1))
                    .filter(date-> {
                        if(entity.getStatus() == ReservationStatus.ACCEPTE)
                            return !(date.isAfter(entity.getPremierJour().minusDays(1)) && date.isBefore(entity.getDernierJour().plusDays(1)));
                        return true;
                    })
                    .forEach(this::ensureDateIsAvailable);
            
            entity.setPremierJour(form.premierJour());
            entity.setDernierJour(form.dernierJour());
        };
        entity.setTelephone(form.telephone());
        entity.setNbPersonne(form.nbPersonne());
        entity.setRepas(new HashMap<>(form.repas()));

        entity.setStatus(status);

        if(statutChanged && entity.getEmail()!=null && !entity.getEmail().isBlank()) {
            try {
                if (entity.getStatus() == ReservationStatus.ACCEPTE)
                    mailService.sendAcceptedMessage(entity);
                else if (entity.getStatus() == ReservationStatus.REFUSE)
                    mailService.sendDeniedMessage(entity);
            } catch (MessagingException ex) {
                System.out.println("Erreur envoi mail: " + ex);
            }
        }

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
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("prenom")), "%" + form.prenom().toLowerCase() + "%"));
            
            if (form.premierJour() != null)
                predicates.add(criteriaBuilder.equal(root.get("premierJour"), form.premierJour()));
        
            if (form.dernierJour() != null)
                predicates.add(criteriaBuilder.equal(root.get("dernierJour"), form.dernierJour()));
        
            if (form.email() != null && !form.email().isBlank())
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + form.email().toLowerCase() + "%"));
        
            if (form.telephone() != null && !form.telephone().isBlank())
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("telephone")), "%" + form.telephone().toLowerCase() + "%"));
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void ensureDateIsAvailable(LocalDate date) {
        Calendrier calendrier = dateNonDispo();
        boolean dateIsReserved = calendrier.datesReservees().contains(date);
        boolean dateIsFermeture = calendrier.datesFermetures().contains(date);

        if(dateIsReserved || dateIsFermeture)
            throw new DatePriseException("Une ou plusieurs dates de l'intervalle ne sont pas disponibles pour la réservation");

    }
    
}
