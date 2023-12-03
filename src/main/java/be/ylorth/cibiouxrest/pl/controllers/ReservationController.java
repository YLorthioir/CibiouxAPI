package be.ylorth.cibiouxrest.pl.controllers;


import be.ylorth.cibiouxrest.bl.exception.NotFoundException;
import be.ylorth.cibiouxrest.bl.models.Calendrier;
import be.ylorth.cibiouxrest.bl.services.ReservationService;
import be.ylorth.cibiouxrest.pl.models.reservation.Reservation;
import be.ylorth.cibiouxrest.pl.models.reservation.ReservationForm;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<Reservation>> getAllWeek(@RequestParam LocalDate lundi, @RequestParam LocalDate diamanche){
        return ResponseEntity.ok(reservationService.getReservationSemaine(lundi, diamanche).stream()
                .map(Reservation::fromEntity)
                .toList());
    }

    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<Reservation> getOne(@PathVariable Long id){
        return ResponseEntity.ok(Reservation.fromEntity(reservationService.getReservation(id).orElseThrow(() -> new NotFoundException("Reservation not found"))));
    }

    @PostMapping("/create")
    public void create(@RequestBody @Valid ReservationForm form){
        reservationService.addReservation(form);
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
