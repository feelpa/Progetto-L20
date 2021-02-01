package it.unipv.ingsw.l20PollingSystem.web;

import it.unipv.ingsw.l20PollingSystem.data.*;
import it.unipv.ingsw.l20PollingSystem.model.Answer;
import it.unipv.ingsw.l20PollingSystem.model.Question;
import it.unipv.ingsw.l20PollingSystem.service.AnswerService;
import it.unipv.ingsw.l20PollingSystem.model.PollViewer;
import it.unipv.ingsw.l20PollingSystem.service.PollViewerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/poll/{id}")
@SessionAttributes("pollViewer")
public class ViewPollController {

    @Autowired
    private PollViewerService pollViewerService;

    @Autowired
    private AnswerService answerService;


    //Metodo che stabilisce che vista mostrare in base alla presenza o meno nel database di una risposta alla specifica domanda.
    @GetMapping //(value = "/{id}")
    public String viewResolver(@PathVariable("id") int pollId, Authentication auth, Model model){
        if((! pollViewerService.isPollOpen(pollId)) || pollViewerService.isQuestionAlreadyAnswered(pollId, auth))
            return showPollResults(pollId, auth, model);
        else
            return showPollForm(pollId, model);
    }

    //Metodo chiamato dal viewResolver nel caso l'utente *non* abbia ancora risposto alla domanda
    private String showPollForm(int pollId, Model model){
        PollViewer pollViewer = pollViewerService.getPollViewerByPollId(pollId);
        model.addAttribute("pollViewer", pollViewer);
        model.addAttribute("answer", new Answer());
        return "pollview";
    }

    //Metodo chiamato dal viewResolver nel caso in cui l'utente abbia già risposto alla domanda
    private String showPollResults(int pollId, Authentication auth, Model model){
        PollViewer pollViewer = pollViewerService.getPollViewerByPollId(pollId);
        Question question = pollViewer.getQuestion();
        Optional<Answer> answer = Optional.ofNullable(answerService.getUserAnswer(auth, question));
        Answer userAnswer = new Answer();
        if (answer.isPresent()) {
            userAnswer = answer.get();
        } else {
            userAnswer = null;
        }
        ArrayList<ResultSet> resultSets = answerService.getStatsByQuestionId(question.getQuestionId());

        model.addAttribute("question", question);
        model.addAttribute("userAnswer", userAnswer);
        model.addAttribute("resultSet", resultSets);
        return "resultview";
    }

    @PostMapping
    public String recordAnswer(@ModelAttribute("answer") @Valid Answer answer, Errors errors,
                               @ModelAttribute("pollViewer") PollViewer pollViewer,
                               Authentication auth, Model model){
        if(errors.hasErrors()){
            model.addAttribute("pollViewer", pollViewer);
            model.addAttribute("answer", answer);
            return "pollview";
        }
        int pollId = pollViewer.getPoll().getPollId();
        answerService.saveAnswerToDatabase(pollId, answer, auth);
        return "redirect:/poll/" + Integer.toString(pollId);
    }
}
