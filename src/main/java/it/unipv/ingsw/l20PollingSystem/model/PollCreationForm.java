package it.unipv.ingsw.l20PollingSystem.model;

import it.unipv.ingsw.l20PollingSystem.web.error.NotEnoughOptionException;
import lombok.Data;

import javax.validation.Valid;
import java.util.ArrayList;

@Data
public class PollCreationForm {

    @Valid
    private Poll poll;

    @Valid
    private Question question;

    private ArrayList<@Valid Choice> choices;

    private ArrayList<String> allowedUsers;


    public PollCreationForm(Poll p, Question q, ArrayList<Choice> c, ArrayList<String> au){
        this.poll = p;
        this.question = q;
        this.choices = c;
        this.allowedUsers = au;
    }

    public PollCreationForm(Poll p, Question q, ArrayList<Choice> c){
        this.poll = p;
        this.question = q;
        this.choices = c;
        this.allowedUsers = new ArrayList<>(1);
    }

    public PollCreationForm(){
        this.poll = new Poll();
        this.question = new Question();

        this.choices = new ArrayList<Choice>(2);
        choices.add(new Choice());
        choices.add(new Choice());

        this.allowedUsers = new ArrayList<String>(1);
        allowedUsers.add(new String());
    }

    public void addOption(Choice choice){
        choices.add(choice);
    }

    public void removeOption(int id) throws NotEnoughOptionException {
        if(choices.size() > 2)
            choices.remove(id);
        else
            throw new NotEnoughOptionException();
    }

    public void addAllowedUser(String allowedUser){
        allowedUsers.add(allowedUser);
    }

    public void removeAllowedUser(int id){
        if(allowedUsers.size() > 1) {
            allowedUsers.remove(id);
        } else {
            poll.setClassified(false);
        }
    }

}
