package it.unipv.ingsw.l20PollingSystem.web;

import it.unipv.ingsw.l20PollingSystem.model.*;
import it.unipv.ingsw.l20PollingSystem.model.PollCreationForm;
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

@Slf4j
@Controller
@RequestMapping("/createpoll")
public class CreatePollController {

    @Autowired
    private PollCreationService pollCreationService;

    @GetMapping
    public String showPollCreationForm(Model model){
        model.addAttribute("form", new PollCreationForm());
        return "pollcreation";

    }

    //aggiunta opzione
    @RequestMapping(params = {"addOption"})
    public String addOption(@ModelAttribute("form") PollCreationForm form, Model model){
        form.addOption(new Choice());
        model.addAttribute("form", form);
        return "pollcreation";
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
        return "pollcreation";
    }

    //aggiunta user
    @RequestMapping(params = {"addUser"})
    public String addUser(@ModelAttribute("form") PollCreationForm form, Model model){
        form.addAllowedUser(new String());
        model.addAttribute("form", form);
        return "pollcreation";
    }

    //rimozione user
    @RequestMapping(params = {"removeUser"})
    public String removeUser(@ModelAttribute("form") PollCreationForm form, @RequestParam("removeUser") int id, Model model){
        form.removeAllowedUser(id);
        model.addAttribute("form", form);
        return "pollcreation";
    }

    @PostMapping
    public String validateAndCreatePoll(@ModelAttribute("form") @Valid PollCreationForm form, BindingResult bindingResult, Authentication auth, Model model){


        //BEGIN FORM VALIDATION
        boolean hasErrors = false;

        //check for basic validation failure
        if(bindingResult.hasErrors()){
            hasErrors = true;
        }

        //check for duplicate choices and remove if found
        if(pollCreationService.checkForDuplicateChoice(form.getChoices())){
            form.setChoices(pollCreationService.removeDuplicateChoice(form.getChoices()));
            bindingResult.addError(new FieldError("form", "choices", "Removed duplicate choice."));
            hasErrors = true;
        }

        //if poll is private
        if(form.getPoll().isClassified()){
            //check for duplicate emails and remove if found
            if(pollCreationService.checkForDuplicateEmail(form.getAllowedUsers())){
                form.setAllowedUsers(pollCreationService.removeDuplicateEmail(form.getAllowedUsers()));
                bindingResult.addError(new FieldError("form", "allowedUsers", "Removed duplicate email."));
                hasErrors = true;
            }
            //loop over emails
            for(String email : form.getAllowedUsers()) {
                int i = form.getAllowedUsers().indexOf(email);
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
        if(pollCreationService.checkForMissingExpirationDate(form)){
            bindingResult.addError(new FieldError("form", "question.expirationDate", "Please select an expiration date for the first round of your poll."));
            hasErrors = true;
        }

        //if any error is found, collect them all and return the pollcreation page
        if(hasErrors) {
            model.addAttribute("form", form);
            return "pollcreation";
        }
        //END FORM VALIDATION

        /* SAVE POLL IN DB
         * once validation is over we can safely save the newly created poll in the database
         */
        pollCreationService.saveNewFormToDatabase(form, auth);

        return "redirect:/user/" + auth.getName();
    }
}