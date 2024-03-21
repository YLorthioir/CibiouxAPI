package be.ylorth.cibiouxrest.pl.controllers;


import be.ylorth.cibiouxrest.bl.models.Calendrier;
import be.ylorth.cibiouxrest.bl.services.ReservationService;
import be.ylorth.cibiouxrest.dal.models.ReservationStatus;
import be.ylorth.cibiouxrest.pl.models.Error;
import be.ylorth.cibiouxrest.pl.models.auth.AuthResponse;
import be.ylorth.cibiouxrest.pl.models.reservation.Reservation;
import be.ylorth.cibiouxrest.pl.models.reservation.ReservationForm;
import be.ylorth.cibiouxrest.pl.models.reservation.ReservationSearchForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Reservations")
@RestController
@RequestMapping("/reservation")
public class ReservationController {
    
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "Get unavailable dates")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Calendrier.class))),
    })
    @GetMapping("/dateNonDispo")
    public ResponseEntity<Calendrier> getDateNonDispo(){
        return ResponseEntity.ok(reservationService.dateNonDispo());
    }

    @Operation(summary = "Get a reservation by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reservation.class))),
            @ApiResponse(responseCode = "404",
                    description = "Not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Reservation not found\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"/reservation/{id}\"}"))
            ),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Unauthorized\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"/reservation/{id}\"}"))
            )
    })
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<Reservation> getOne(@PathVariable Long id){
        return ResponseEntity.ok(Reservation.fromEntity(reservationService.getReservation(id).orElseThrow(() -> new EntityNotFoundException("Reservation not found"))));
    }

    @Operation(summary = "Get pending reservations")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = Reservation.class)
                            )
                    )
            ),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Unauthorized\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"/reservation/getPendings\"}"))
            )
    })
    @GetMapping("/getPendings")
    public ResponseEntity<List<Reservation>> getPendings(){
        return ResponseEntity.ok(reservationService.getPendings().stream().map(Reservation::fromEntity).toList());
    }

    @Operation(summary = "Get a reservation by date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Reservation.class))),
            @ApiResponse(responseCode = "404",
                    description = "Not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Reservation not found\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"/reservation/{date}\"}"))
            ),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Unauthorized\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"/reservation/{date}\"}"))
            )
    })
    @GetMapping("/getByDate/{date}")
    public ResponseEntity<Reservation> getOneBydate(@PathVariable LocalDate date){
        return ResponseEntity.ok(Reservation.fromEntity(reservationService.getOneByDate(date).orElseThrow(() -> new EntityNotFoundException("Reservation not found"))));
    }

    @Operation(summary = "Get reservations by criteria")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = Reservation.class)
                            )
                    )
            ),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Unauthorized\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"/reservation/search\"}"))
            )
    })
    @PostMapping("/search")
    public ResponseEntity<List<Reservation>> search(@RequestBody ReservationSearchForm form){
        return ResponseEntity.ok(reservationService.search(form).stream().map(Reservation::fromEntity).toList());
    }

    @Operation(summary = "Create a new visitor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Visitor created successfully"),
            @ApiResponse(responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Les dates ne peuvent être nulles\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"/reservation/createVisitor\"}"))
            )
    })
    @PostMapping("/createVisitor")
    @ResponseStatus(HttpStatus.CREATED)
    public void createVisitor(@RequestBody @Valid @Parameter(description = "Reservation details", required = true) ReservationForm form) {
        reservationService.addReservationVisitor(form);
    }

    @Operation(summary = "Create a new visitor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Admin created successfully"),
            @ApiResponse(responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Les dates ne peuvent être nulles\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"/reservation/createDirection\"}"))
            ),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Unauthorized\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"/reservation/search\"}"))
            )
    })
    @PostMapping("/createDirection")
    @ResponseStatus(HttpStatus.CREATED)
    public void createDirection(@RequestBody @Valid @Parameter(description = "Reservation details", required = true) ReservationForm form){
        reservationService.addReservationDirection(form);
    }

    @Operation(summary = "Update reservation status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation status updated successfully"),
            @ApiResponse(responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Les dates ne peuvent être nulles\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"/reservation/{id:[0-9]+}/updateStatus\"}"))
            ),
            @ApiResponse(responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"status can't be null\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"/reservation/{id:[0-9]+}\"}"))
            ),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Unauthorized\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"/reservation/{id:[0-9]+}/updateStatus"))
            )
    })
    @PatchMapping("/{id:[0-9]+}/updateStatus")
    public void updateReservationStatus(
            @PathVariable @Parameter(description = "Reservation's ID", required = true) Long id,
            @RequestParam @Parameter(description = "New reservation status", required = true) String status
    ) {
        reservationService.changeReservationStatus(id, ReservationStatus.valueOf(status));
    }

    @Operation(summary = "Update reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation updated successfully"),
            @ApiResponse(responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"status can't be null\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"/reservation/{id:[0-9]+}\"}"))
            ),
            @ApiResponse(responseCode = "404",
                    description = "Not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Reservation not found\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"/reservation/{id:[0-9]+}\"}"))
            ),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Unauthorized\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"{id:[0-9]+}"))
            )
    })
    @PutMapping("/{id:[0-9]+}")
    public void update(@RequestBody @Valid @Parameter(description = "Reservation form", required = true) ReservationForm form, @PathVariable @Parameter(description = "Reservation's ID", required = true) Long id, @RequestParam @Parameter(description = "Reservation status", required = true) String status){
        reservationService.updateReservation(id, form, ReservationStatus.valueOf(status));
    }

    @Operation(summary = "Delete reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation deleted successfully"),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Unauthorized\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"/reservation/{id:[0-9]+}"))
            )
    })
    @DeleteMapping("/{id:[0-9]+}")
    public void delete(@PathVariable Long id){
        reservationService.deleteReservation(id);
    }
}
