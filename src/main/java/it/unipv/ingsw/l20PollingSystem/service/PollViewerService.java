package it.unipv.ingsw.l20PollingSystem.service;

import it.unipv.ingsw.l20PollingSystem.data.*;
import it.unipv.ingsw.l20PollingSystem.model.Choice;
import it.unipv.ingsw.l20PollingSystem.model.Poll;
import it.unipv.ingsw.l20PollingSystem.model.PollViewer;
import it.unipv.ingsw.l20PollingSystem.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class PollViewerService {

    @Autowired
    private PollRepository pollRepo;
    @Autowired
    private QuestionRepository questionRepo;
    @Autowired
    private ChoiceRepository choiceRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private AnswerRepository answerRepo;


    public boolean isQuestionAlreadyAnswered(int pollId, Authentication auth) {
        int questionId = questionRepo.findByPollIdAndIsOpen(pollId, true).getQuestionId();
        int userId = userRepo.findByUsername(auth.getName()).getUserId();
        return (answerRepo.existsByQuestionIdAndUserId(questionId, userId));
    }

    public boolean isPollOpen(int pollId){
        return (pollRepo.existsByPollIdAndIsOpen(pollId, true));
    }

    public PollViewer getPollViewerByPollId(int pollId){
        Optional<Poll> optionalPoll = pollRepo.findById(pollId);
        if (optionalPoll.isPresent()) {
            Poll p = optionalPoll.get();
            Question q = questionRepo.findByPollIdAndIsOpen(pollId, true);
            ArrayList<Choice> c = choiceRepo.findAllByQuestionId(q.getQuestionId());
            return new PollViewer(p, q, c);
        } else
            return new PollViewer();
    }

    public ArrayList<PollViewer> getPollsCreatedByUser(int userId){
        ArrayList<Poll> polls = pollRepo.findAllByCreatorId(userId);
        ArrayList<PollViewer> pollsCreatedByUser = getPollViewerList(polls);
        return pollsCreatedByUser;
    }

    public ArrayList<PollViewer> getPollsSharedWithUser(int userId){
        ArrayList<Poll> polls = pollRepo.findAllSharedPoll(userId);
        ArrayList<PollViewer> pollsSharedWithUser = getPollViewerList(polls);
        return pollsSharedWithUser;
    }

    public ArrayList<PollViewer> getPublicPolls(int userId){
        ArrayList<Poll> polls = pollRepo.findAllByClassifiedAndCreatorIdIsNot(false, userId);
        ArrayList<PollViewer> publicPolls = getPollViewerList(polls);
        return publicPolls;
    }

    private ArrayList<PollViewer> getPollViewerList(ArrayList<Poll> polls){
        ArrayList<PollViewer> pollViewers = new ArrayList<>();
        for (Poll p: polls) {
            Question q = questionRepo.findByPollIdAndIsOpen(p.getPollId(), true);
            ArrayList<Choice> c = choiceRepo.findAllByQuestionId(q.getQuestionId());
            pollViewers.add(new PollViewer(p, q, c));
        }
        pollViewers.trimToSize();
        return pollViewers;
    }

}