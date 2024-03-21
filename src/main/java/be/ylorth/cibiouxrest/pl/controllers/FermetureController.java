package be.ylorth.cibiouxrest.pl.controllers;

import be.ylorth.cibiouxrest.bl.services.FermetureService;
import be.ylorth.cibiouxrest.pl.models.Error;
import be.ylorth.cibiouxrest.pl.models.Fermeture;
import be.ylorth.cibiouxrest.pl.models.auth.AuthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Set;

@Tag(name = "Fermetures")
@RestController
@RequestMapping("/fermeture")
public class FermetureController {
    private final FermetureService fermetureService;

    public FermetureController(FermetureService fermetureService) {
        this.fermetureService = fermetureService;
    }

    @Operation(summary = "insert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created"),
            @ApiResponse(responseCode = "400",
                    description = "Bad request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Les dates ne peuvent être nulles\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"/fermeture/insert\"}"))
            )
    })
    @PostMapping("/insert")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @NotEmpty Set<LocalDate> dates){
        fermetureService.create(dates);
    }

    @Operation(summary = "delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "403",
                    description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Error.class),
                            examples = @ExampleObject(value = "{\"message\": \"Unauthorized\", \"requestMadeAt\": \"2023-03-20T14:28:23.23\", \"URI\": \"/fermeture/delete/{id}\"}"))
            )
    })
    @DeleteMapping("/delete")
    public void delete(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        fermetureService.delete(date);
    }
}
