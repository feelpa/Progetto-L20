package it.unipv.ingsw.l20PollingSystem.security;

import it.unipv.ingsw.l20PollingSystem.data.AllowedUserRepository;
import it.unipv.ingsw.l20PollingSystem.data.PollRepository;
import it.unipv.ingsw.l20PollingSystem.data.UserRepository;
import it.unipv.ingsw.l20PollingSystem.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class WebSecurity {

    private final UserRepository userRepo;
    private final AllowedUserRepository allowedUserRepo;
    private final PollRepository pollRepo;

    @Autowired
    public WebSecurity(UserRepository userRepo, AllowedUserRepository allowedUserRepo, PollRepository pollRepo){
        this.userRepo = userRepo;
        this.allowedUserRepo = allowedUserRepo;
        this.pollRepo = pollRepo;
    }

    public boolean checkUsername(String username){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (username.equals(auth.getName()));
    }

    public boolean checkPollAuth(int pollId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!(auth instanceof AnonymousAuthenticationToken)) {
            User user = userRepo.findByUsername(auth.getName());
            int userId = user.getUserId();
            boolean isPrivate = pollRepo.findByPollId(pollId).isClassified();
            boolean isCreator = pollRepo.existsPollByPollIdAndCreatorId(pollId, userId);
            boolean isShared = allowedUserRepo.existsAllowedUserByIdUserIdAndIdPollId(userId, pollId);
            return (!isPrivate || isCreator || isShared);
        } else
            return false;
    }

    public boolean isCreator(int pollId){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByUsername(auth.getName());
        return pollRepo.existsPollByPollIdAndCreatorId(pollId, user.getUserId());
    }
}
