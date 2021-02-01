package it.unipv.ingsw.l20PollingSystem.service;

import it.unipv.ingsw.l20PollingSystem.data.*;
import it.unipv.ingsw.l20PollingSystem.model.Answer;
import it.unipv.ingsw.l20PollingSystem.model.Question;
import it.unipv.ingsw.l20PollingSystem.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class AnswerService {
    @Autowired
    private QuestionRepository questionRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private AnswerRepository answerRepo;


    public void saveAnswerToDatabase(int pollId, Answer answer, Authentication auth){
        int userId = userRepo.findByUsername(auth.getName()).getUserId();
        answer.setPollId(pollId);
        answer.setQuestionId(questionRepo.findByPollIdAndIsOpen(pollId, true).getQuestionId());
        answer.setUserId(userId);
        answer.setCastingTime(LocalDateTime.now());
        answerRepo.save(answer);
    }

    public Answer getUserAnswer(Authentication auth, Question question) {
        User user = userRepo.findByUsername(auth.getName());
        Answer answer = answerRepo.findByUserIdAndQuestionId(user.getUserId(), question.getQuestionId());
        return answer;
    }

    public ArrayList<ResultSet> getStatsByQuestionId(int questionId){
        return answerRepo.getResult(questionId);
    }
}
