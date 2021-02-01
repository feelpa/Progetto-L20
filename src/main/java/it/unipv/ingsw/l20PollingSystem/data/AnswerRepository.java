package it.unipv.ingsw.l20PollingSystem.data;

import it.unipv.ingsw.l20PollingSystem.model.Answer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface AnswerRepository extends CrudRepository<Answer, Integer> {

    public Answer findByUserIdAndQuestionId(int userId, int questionId);

    public ArrayList<Answer> findAllByPollId(int pollId);

    public boolean existsByQuestionIdAndUserId(int questionId, int userId);

    public boolean existsByPollId(int pollId);

    @Query(value = "SELECT CHOICE_ID as ID, CTEXT, COALESCE(`COUNT`, 0) as `COUNT`, " +
            "CONCAT(ROUND(COALESCE((`COUNT`/ (SELECT DISTINCT COUNT(*) FROM answer WHERE QUESTION_ID = :questionId) * 100 ), 0), 0),'%') as PERCENTAGE " +
            "FROM ( " +
            "(SELECT CTEXT, CHOICE_ID " +
            "FROM choice " +
            "WHERE QUESTION_ID = :questionId) as A " +
            "NATURAL LEFT JOIN " +
            "(SELECT CHOICE_ID, COUNT(ANSWER_ID) as `COUNT` " +
            "FROM answer " +
            "WHERE QUESTION_ID = :questionId " +
            "GROUP BY CHOICE_ID) as B);", nativeQuery = true)
    public ArrayList<ResultSet> getResult(int questionId);

}
