package be.ylorth.cibiouxrest.pl.controllers;

import be.ylorth.cibiouxrest.bl.exception.DatePriseException;
import be.ylorth.cibiouxrest.pl.models.Error;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ControllerAdvisor {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Error> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest req){

        Error errorDTO = new Error(
                ex.getMessage(),
                LocalDateTime.now(),
                req.getRequestURI());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_JSON );

        return ResponseEntity.status( HttpStatus.UNAUTHORIZED)
                .headers( headers )
                .body( errorDTO );

    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Error> handleEntityNotFoundException(EntityNotFoundException ex, HttpServletRequest req){

        Error errorDTO = new Error(
                ex.getMessage(),
                LocalDateTime.now(),
                req.getRequestURI());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_JSON );

        return ResponseEntity.status( HttpStatus.NOT_FOUND)
                .headers( headers )
                .body( errorDTO );

    }

    @ExceptionHandler(DatePriseException.class)
    public ResponseEntity<Error> handleDatePriseException(DatePriseException ex, HttpServletRequest req){

        Error errorDTO = new Error(
                ex.getMessage(),
                LocalDateTime.now(),
                req.getRequestURI());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_JSON );

        return ResponseEntity.status( HttpStatus.BAD_REQUEST)
                .headers( headers )
                .body( errorDTO );

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest req){

        Error errorDTO = new Error(
                ex.getMessage(),
                LocalDateTime.now(),
                req.getRequestURI());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_JSON );

        return ResponseEntity.status( HttpStatus.BAD_REQUEST)
                .headers( headers )
                .body( errorDTO );

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Error> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest req){

        BindingResult result = ex.getBindingResult();
        
        String errorMessage = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        Error errorDTO = new Error(
                errorMessage,
                LocalDateTime.now(),
                req.getRequestURI());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_JSON );

        return ResponseEntity.status( HttpStatus.BAD_REQUEST)
                .headers( headers )
                .body( errorDTO );

    }
}
