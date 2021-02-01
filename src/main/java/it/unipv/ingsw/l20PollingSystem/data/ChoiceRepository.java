package it.unipv.ingsw.l20PollingSystem.data;

import it.unipv.ingsw.l20PollingSystem.model.Choice;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface ChoiceRepository extends CrudRepository<Choice, Integer> {

    public Choice findByChoiceIdAndQuestionId(int choiceId, int questionId);
    public ArrayList<Choice> findAllByQuestionId(int questionId);

}
