package it.unipv.ingsw.l20PollingSystem.security.error;

public class EmailAlreadyExistException extends RuntimeException{

    public EmailAlreadyExistException(){
        super("Email already in use.");
    }

}
