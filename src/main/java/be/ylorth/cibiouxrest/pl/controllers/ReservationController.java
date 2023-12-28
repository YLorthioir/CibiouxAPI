package be.ylorth.cibiouxrest.pl.controllers;


import be.ylorth.cibiouxrest.bl.exception.NotFoundException;
import be.ylorth.cibiouxrest.bl.models.Calendrier;
import be.ylorth.cibiouxrest.bl.services.ReservationService;
import be.ylorth.cibiouxrest.dal.models.ReservationStatus;
import be.ylorth.cibiouxrest.pl.models.reservation.Reservation;
import be.ylorth.cibiouxrest.pl.models.reservation.ReservationForm;
import be.ylorth.cibiouxrest.pl.models.reservation.ReservationSearchForm;
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
    
    @GetMapping("/all")
    public ResponseEntity<List<Reservation>> getAllWeek(@RequestParam LocalDate lundi, @RequestParam LocalDate dimanche){
        return ResponseEntity.ok(reservationService.getReservationSemaine(lundi, dimanche).stream()
                .map(Reservation::fromEntity)
                .toList());
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<Reservation> getOne(@PathVariable Long id){
        return ResponseEntity.ok(Reservation.fromEntity(reservationService.getReservation(id).orElseThrow(() -> new NotFoundException("Reservation not found"))));
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
    public void update(@RequestBody @Valid ReservationForm form, @PathVariable Long id){
        reservationService.updateReservation(id, form);
    }

    @DeleteMapping("/{id:[0-9]+}")
    public void delete(@PathVariable Long id){
        reservationService.deleteReservation(id);
    }
}
