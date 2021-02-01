package it.unipv.ingsw.l20PollingSystem.service;

import it.unipv.ingsw.l20PollingSystem.data.ConfirmationTokenRepository;
import it.unipv.ingsw.l20PollingSystem.model.ConfirmationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfirmationTokenService extends ExpirationControl{

    @Autowired
    ConfirmationTokenRepository confirmationTokenRepo;

    public ConfirmationToken saveConfirmationToken(ConfirmationToken token){
        return confirmationTokenRepo.save(token);
    }

    public void deleteConfirmationToken(ConfirmationToken token){
        confirmationTokenRepo.delete(token);
    }

    public ConfirmationToken getConfirmationTokenByToken(String token){
        return confirmationTokenRepo.findByConfirmationToken(token);
    }

}
