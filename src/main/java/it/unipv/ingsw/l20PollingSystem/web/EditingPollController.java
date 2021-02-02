package it.unipv.ingsw.l20PollingSystem.web;

import it.unipv.ingsw.l20PollingSystem.data.AnswerRepository;
import it.unipv.ingsw.l20PollingSystem.data.PollRepository;
import it.unipv.ingsw.l20PollingSystem.data.QuestionRepository;
import it.unipv.ingsw.l20PollingSystem.data.UserRepository;
import it.unipv.ingsw.l20PollingSystem.model.Choice;
import it.unipv.ingsw.l20PollingSystem.model.Poll;
import it.unipv.ingsw.l20PollingSystem.model.PollCreationForm;
import it.unipv.ingsw.l20PollingSystem.model.Question;
import it.unipv.ingsw.l20PollingSystem.service.PollCreationService;
import it.unipv.ingsw.l20PollingSystem.web.error.NotEnoughOptionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/edit/poll/{id}")
@SessionAttributes("form")
public class EditingPollController {

    @Autowired
    private PollRepository pollRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private AnswerRepository answerRepo;
    @Autowired
    private QuestionRepository questionRepo;
    @Autowired
    private PollCreationService pollCreationService;

    @GetMapping
    public String showPollEditingForm(@PathVariable("id") int pollId, Model model){
        if(pollRepo.existsByPollIdAndIsOpen(pollId, true)
                & !(answerRepo.existsByPollId(pollId))) {
            PollCreationForm form = pollCreationService.retrievePollForEditing(pollId);
            model.addAttribute("form", form);
            return "pollediting";
        } else
            model.addAttribute("message", "You cannot edit a closed poll or one a ballot has already been casted for.");
            return "failure";
    }

    //aggiunta opzione
    @RequestMapping(params = {"addOption"})
    public String addOption(@ModelAttribute("form") PollCreationForm form, Model model){
        form.addOption(new Choice());
        model.addAttribute("form", form);
        return "pollediting";
    }

    //rimozione opzione
    @RequestMapping(params = {"removeOption"})
    public String removeOption(@ModelAttribute("form") PollCreationForm form, BindingResult bindingResult,
                               @RequestParam("removeOption") int id, Model model){
        try {
            form.removeOption(id);
        } catch (NotEnoughOptionException neoEx) {
            bindingResult.addError(new FieldError("form", "choices", neoEx.getMessage()));
        }
        model.addAttribute("form", form);
        return "pollediting";
    }

    //aggiunta user
    @RequestMapping(params = {"addUser"})
    public String addUser(@ModelAttribute("form") PollCreationForm form, Model model){
        form.addAllowedUser(new String());
        model.addAttribute("form", form);
        return "pollediting";
    }

    //rimozione user
    @RequestMapping(params = {"removeUser"})
    public String removeUser(@ModelAttribute("form") PollCreationForm form, @RequestParam("removeUser") int id, Model model){
        form.removeAllowedUser(id);
        model.addAttribute("form", form);
        return "pollediting";
    }

    @PostMapping
    public String validateAndUpdatePoll(@ModelAttribute("form") @Valid PollCreationForm currentForm, BindingResult bindingResult,
                                        @PathVariable("id") int pollId, Authentication auth, Model model){

        //BEGIN FORM VALIDATION
        boolean hasErrors = false;

        //check for basic validation failure
        if(bindingResult.hasErrors()){
            hasErrors = true;
        }

        //check for duplicate choices and remove if found
        if(pollCreationService.checkForDuplicateChoice(currentForm.getChoices())){
            currentForm.setChoices(pollCreationService.removeDuplicateChoice(currentForm.getChoices()));
            bindingResult.addError(new FieldError("form", "choices", "Removed duplicate choice."));
            hasErrors = true;
        }

        //if poll is private
        if(currentForm.getPoll().isClassified()){
            //check for duplicate emails and remove if found
            if(pollCreationService.checkForDuplicateEmail(currentForm.getAllowedUsers())){
                currentForm.setAllowedUsers(pollCreationService.removeDuplicateEmail(currentForm.getAllowedUsers()));
                bindingResult.addError(new FieldError("form", "allowedUsers", "Removed duplicate email."));
                hasErrors = true;
            }
            //loop over emails
            for(String email : currentForm.getAllowedUsers()) {
                int i = currentForm.getAllowedUsers().indexOf(email);
                //check if any is blank
                if(pollCreationService.checkForBlankEmail(email)){
                    bindingResult.addError(new FieldError("form", "allowedUsers[" + i + "]", "Must provide a valid email address."));
                    hasErrors = true;
                }
                //check if any is non-present in the database
                else if(pollCreationService.checkForNonExistentEmail(email)){
                    bindingResult.addError(new FieldError("form", "allowedUsers[" + i + "]", "Email [" + email + "] not found. May be misspelled or not registered yet."));
                    hasErrors = true;
                }
            }
        }

        //chek if expiration dates are set for both poll and question in case poll is double round
        if(pollCreationService.checkForMissingExpirationDate(currentForm)){
            bindingResult.addError(new FieldError("form", "question.expirationDate", "Please select an expiration date for the first round of your poll."));
            hasErrors = true;
        }

        //if any error is found, collect them all and return the pollcreation page
        if(hasErrors) {
            model.addAttribute("form", currentForm);
            return "pollediting";
        }
        //END FORM VALIDATION

        //check differences between polls
        PollCreationForm previousForm = pollCreationService.retrievePollForEditing(pollId);
        PollCreationForm form = pollCreationService.checkForDifferencesInForms(previousForm, currentForm);

        /* SAVE POLL IN DB
         * once validation is over we can safely save the edited poll in the database
         */
        pollCreationService.updatePoll(form);

        return "redirect:/user/" + auth.getName();
    }

    @GetMapping(value = "/second-round")
    public String startSecondRound(@PathVariable("id") int pollId, Model model){
        Poll poll = pollRepo.findByPollId(pollId);
        if(!poll.isOpen()){
            model.addAttribute("message", "Operation aborted.\nThe poll is closed.");
            return "failure";
        } else if(poll.isDoubleRound()) {
            Question question = questionRepo.findByPollIdAndIsOpen(pollId, true);
            if(!(question.getQText().contains("[SECOND ROUND]"))) {
                PollCreationForm secondRoundForm = pollCreationService.setUpSecondRound(poll, question);
                pollCreationService.updatePoll(secondRoundForm);
                return "redirect:/poll/" + pollId;
            } else {
                model.addAttribute("message", "Operation aborted.\nAlready second round.");
                return "failure";
            }
        } else {
            model.addAttribute("message", "Operation aborted. This is not a double round poll.\nNice try!");
            return "failure";
        }
    }

    @GetMapping(value = "/stop-the-count")
    public String stopTheCount(@PathVariable("id") int pollId, Model model){
        Poll poll = pollRepo.findByPollId(pollId);
        if(!poll.isOpen()){
            model.addAttribute("message", "Operation aborted.\nThe poll is closed.");
            return "failure";
        } else {
            poll.setOpen(false);
            poll.setExpirationDate(LocalDateTime.now());
            pollRepo.save(poll);
            return "redirect:/user/";
        }
    }

    @GetMapping(value = "/delete")
    public String deletePoll(@PathVariable("id") int pollId, Model model){
        Optional<Poll> optionalPollToDelete = pollRepo.findById(pollId);
        if(optionalPollToDelete.isPresent()){
            Poll pollToDelete = optionalPollToDelete.get();
            pollCreationService.deletePoll(pollToDelete);
            return "redirect:/user/";
        } else {
            model.addAttribute("message", "Operation failed");
            return "failure";
        }
    }

}
