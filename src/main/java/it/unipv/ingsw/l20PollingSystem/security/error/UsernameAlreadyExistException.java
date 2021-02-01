package it.unipv.ingsw.l20PollingSystem.security.error;

public class UsernameAlreadyExistException extends RuntimeException{

    public UsernameAlreadyExistException(){
        super("Username already in use.");
    }

}
