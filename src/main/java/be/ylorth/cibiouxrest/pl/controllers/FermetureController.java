package be.ylorth.cibiouxrest.pl.controllers;

import be.ylorth.cibiouxrest.bl.services.FermetureService;
import be.ylorth.cibiouxrest.pl.models.Fermeture;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("/fermeture")
public class FermetureController {
    private final FermetureService fermetureService;

    public FermetureController(FermetureService fermetureService) {
        this.fermetureService = fermetureService;
    }
    
    @PostMapping("/insert")
    public void create(@RequestBody @NotEmpty Set<LocalDate> dates){
        fermetureService.create(dates);
    }
    
    @DeleteMapping("/delete")
    public void delete(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        fermetureService.delete(date);
    }
}
