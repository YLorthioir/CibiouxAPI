package be.ylorth.cibiouxrest.pl.controllers;

import be.ylorth.cibiouxrest.pl.models.Error;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Error> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest req){

        Error errorDTO = new Error(
                ex.getMessage(),
                HttpStatus.UNAUTHORIZED,
                LocalDateTime.now(),
                req.getRequestURI());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_JSON );

        return ResponseEntity.status( HttpStatus.UNAUTHORIZED)
                .headers( headers )
                .body( errorDTO );

    }
}
