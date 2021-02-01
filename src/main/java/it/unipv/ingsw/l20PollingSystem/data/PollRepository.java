package it.unipv.ingsw.l20PollingSystem.data;

import it.unipv.ingsw.l20PollingSystem.model.Poll;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface PollRepository extends CrudRepository<Poll, Integer> {

    public Poll findByPollId(int pollId);

    public ArrayList<Poll> findAllByClassifiedAndCreatorIdIsNot(boolean bool, int creatorId);

    public ArrayList<Poll> findAllByCreatorId(int creatorId);

    public ArrayList<Poll> findAllByIsOpen(boolean bool);

    @Query(value="SELECT * " +
            "FROM " +
            "(SELECT POLL_ID " +
            "FROM allowed_user " +
            "WHERE USER_ID = :userId) as A " +
            "NATURAL JOIN poll", nativeQuery = true)
    public ArrayList<Poll> findAllSharedPoll(int userId);

    public boolean existsPollByPollIdAndCreatorId(int pollId, int creatorId);

    public boolean existsByPollIdAndIsOpen(int pollId, boolean bool);

}
