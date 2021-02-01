package it.unipv.ingsw.l20PollingSystem.data;

import it.unipv.ingsw.l20PollingSystem.model.ConfirmationToken;
import org.springframework.data.repository.CrudRepository;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, Integer> {
    ConfirmationToken findByConfirmationToken(String token);
}
