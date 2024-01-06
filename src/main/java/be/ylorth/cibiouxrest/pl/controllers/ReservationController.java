package be.ylorth.cibiouxrest.pl.controllers;


import be.ylorth.cibiouxrest.bl.models.Calendrier;
import be.ylorth.cibiouxrest.bl.services.ReservationService;
import be.ylorth.cibiouxrest.dal.models.ReservationStatus;
import be.ylorth.cibiouxrest.pl.models.reservation.Reservation;
import be.ylorth.cibiouxrest.pl.models.reservation.ReservationForm;
import be.ylorth.cibiouxrest.pl.models.reservation.ReservationSearchForm;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
    
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }
    
    @GetMapping("/dateNonDispo")
    public ResponseEntity<Calendrier> getDateNonDispo(){
        return ResponseEntity.ok(reservationService.dateNonDispo());
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<Reservation> getOne(@PathVariable Long id){
        return ResponseEntity.ok(Reservation.fromEntity(reservationService.getReservation(id).orElseThrow(() -> new EntityNotFoundException("Reservation not found"))));
    }

    @GetMapping("/getPendings")
    public ResponseEntity<List<Reservation>> getPendings(){
        return ResponseEntity.ok(reservationService.getPendings().stream().map(Reservation::fromEntity).toList());
    }

    @GetMapping("/getByDate/{date}")
    public ResponseEntity<Reservation> getOneBydate(@PathVariable LocalDate date){
        return ResponseEntity.ok(Reservation.fromEntity(reservationService.getOneByDate(date).orElseThrow(() -> new EntityNotFoundException("Reservation not found"))));
    }
    
    @PostMapping("/search")
    public ResponseEntity<List<Reservation>> search(@RequestBody ReservationSearchForm form){
        return ResponseEntity.ok(reservationService.search(form).stream().map(Reservation::fromEntity).toList());
    }

    @PostMapping("/createVisitor")
    @ResponseStatus(HttpStatus.CREATED)
    public void createVisitor(@RequestBody @Valid ReservationForm form){
        reservationService.addReservationVisitor(form);
    }

    @PostMapping("/createDirection")
    @ResponseStatus(HttpStatus.CREATED)
    public void createDirection(@RequestBody @Valid ReservationForm form){
        reservationService.addReservationDirection(form);
    }
    
    @PutMapping("/{id:[0-9]+}/updateStatus")
    public void updateReservationStatus(@PathVariable Long id, @RequestParam String status){
        reservationService.changeReservationStatus(id, ReservationStatus.valueOf(status));
    }
    
    @PutMapping("/{id:[0-9]+}")
    public void update(@RequestBody @Valid ReservationForm form, @PathVariable Long id, @RequestParam String status){
        reservationService.updateReservation(id, form, ReservationStatus.valueOf(status));
    }

    @DeleteMapping("/{id:[0-9]+}")
    public void delete(@PathVariable Long id){
        reservationService.deleteReservation(id);
    }
}
