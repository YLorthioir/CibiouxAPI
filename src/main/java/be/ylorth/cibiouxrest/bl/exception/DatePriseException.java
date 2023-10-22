package be.ylorth.cibiouxrest.bl.exception;

import lombok.Getter;

@Getter
public class DatePriseException extends RuntimeException{

    private final String message;
    public DatePriseException(String message){
        super("Date déjà prise!");
        this.message = message;
    }
}
