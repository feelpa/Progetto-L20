package it.unipv.ingsw.l20PollingSystem.service;

import it.unipv.ingsw.l20PollingSystem.model.Expirable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

abstract class ExpirationControl {

    public boolean checkExpiration(Expirable expirable){
        LocalDateTime expirationDate = expirable.getExpirationDate();
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime tempDateTime = LocalDateTime.from(now);

        long years = tempDateTime.until(expirationDate, ChronoUnit.YEARS);
        tempDateTime = tempDateTime.plusYears(years);

        long months = tempDateTime.until(expirationDate, ChronoUnit.MONTHS);
        tempDateTime = tempDateTime.plusMonths(months);

        long days = tempDateTime.until(expirationDate, ChronoUnit.DAYS);
        tempDateTime = tempDateTime.plusDays(days);

        long hours = tempDateTime.until(expirationDate, ChronoUnit.HOURS);
        tempDateTime = tempDateTime.plusHours(hours);

        long minutes = tempDateTime.until(expirationDate, ChronoUnit.MINUTES);
        tempDateTime = tempDateTime.plusMinutes( minutes );

        long seconds = tempDateTime.until(expirationDate, ChronoUnit.SECONDS);

        return (years < 0 || months < 0 || days < 0 || hours < 0 || minutes <= 0);
    }

}
