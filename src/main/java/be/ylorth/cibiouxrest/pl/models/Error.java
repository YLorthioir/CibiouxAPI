package be.ylorth.cibiouxrest.pl.models;

import java.time.LocalDateTime;

public record Error(
        String message,
        LocalDateTime requestMadeAt,
        String URI
    ){
}
