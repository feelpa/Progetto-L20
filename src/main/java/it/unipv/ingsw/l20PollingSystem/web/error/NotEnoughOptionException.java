package it.unipv.ingsw.l20PollingSystem.web.error;

public class NotEnoughOptionException extends RuntimeException{

    public NotEnoughOptionException(){
        super("You must provide at least two options.");
    }
}
