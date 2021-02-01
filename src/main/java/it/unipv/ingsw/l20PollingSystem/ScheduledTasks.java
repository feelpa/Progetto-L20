package it.unipv.ingsw.l20PollingSystem;

import it.unipv.ingsw.l20PollingSystem.data.*;
import it.unipv.ingsw.l20PollingSystem.model.ConfirmationToken;
import it.unipv.ingsw.l20PollingSystem.model.Poll;
import it.unipv.ingsw.l20PollingSystem.model.PollCreationForm;
import it.unipv.ingsw.l20PollingSystem.model.Question;
import it.unipv.ingsw.l20PollingSystem.service.PollCreationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Slf4j
@Component
public class ScheduledTasks {

    @Autowired
    private PollRepository pollRepo;
    @Autowired
    private QuestionRepository questionRepo;
    @Autowired
    private PollCreationService pollCreationService;


    @Scheduled(cron = "0 */5 * ? * *")    //every five minutes.             (cron = "0 * * ? * *") every minute.
    public void closeExpiredPollsAndGenerateSecondRoundForExpiredQuestion(){
        ArrayList<Poll> polls = pollRepo.findAllByIsOpen(true);
        for(Poll poll : polls) {
            pollCreationService.closeExpiredPoll(poll);
            if(poll.isDoubleRound()) {
                Question firstRoundQuestion = questionRepo.findByPollIdAndIsOpen(poll.getPollId(), true);
                if(pollCreationService.checkExpiration(firstRoundQuestion)) {
                    PollCreationForm form = pollCreationService.setUpSecondRound(poll, firstRoundQuestion);
                    pollCreationService.updatePoll(form);
                }
            }
        }
    }
}