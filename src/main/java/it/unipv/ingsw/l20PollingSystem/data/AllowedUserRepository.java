package it.unipv.ingsw.l20PollingSystem.data;

import it.unipv.ingsw.l20PollingSystem.model.AllowedUser;
import it.unipv.ingsw.l20PollingSystem.model.AllowedUserKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface AllowedUserRepository extends CrudRepository<AllowedUser, AllowedUserKey> {

    public boolean existsAllowedUserByIdUserIdAndIdPollId(int userId, int pollId);

    ArrayList<AllowedUser> findAllByIdPollId(int pollId);

}