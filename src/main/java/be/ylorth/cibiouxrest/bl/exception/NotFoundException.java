package be.ylorth.cibiouxrest.bl.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException{

    private final String message;
    public NotFoundException(String message){
        super("Pas trouvé");
        this.message = message;
    }
}
