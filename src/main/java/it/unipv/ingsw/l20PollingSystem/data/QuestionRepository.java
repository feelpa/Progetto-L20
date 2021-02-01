package it.unipv.ingsw.l20PollingSystem.data;

import it.unipv.ingsw.l20PollingSystem.model.Question;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface QuestionRepository extends CrudRepository<Question, Integer> {

    public Question findByPollIdAndIsOpen(int pollId, boolean bool);

    public ArrayList<Question> findAllByPollId(int pollId);

}
