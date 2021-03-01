package it.unipv.ingsw.l20PollingSystem;

import it.unipv.ingsw.l20PollingSystem.model.Choice;
import it.unipv.ingsw.l20PollingSystem.model.PollCreationForm;
import it.unipv.ingsw.l20PollingSystem.service.PollCreationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;

import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class validationTest {

    private PollCreationService pollCreationService = new PollCreationService();

    @MockBean
    private PollCreationForm pollCreationForm;

    @Test
    public void checkForDuplicateChoiceTest(){

        int choiceCapacity = 3;
        ArrayList<Choice> choices = new ArrayList<Choice>(choiceCapacity);
        for (int i = 0; i < choiceCapacity; i++) {
            choices.add(new Choice());
            choices.get(i).setQuestionId(1);
            choices.get(i).setChoiceId(i);
            choices.get(i).setCText("choice-id: " + Integer.toString(i));
        }

        given(this.pollCreationForm.getChoices()).willReturn(choices);
        assert pollCreationService.checkForDuplicateChoice(pollCreationForm.getChoices()) == false;

    }

    @Test
    public void checkForDuplicateEmailTest(){
        ArrayList<String> emails = new ArrayList<>();
        emails.add("pippo@example.com");
        emails.add("pluto@example.com");
        emails.add("paperino@example.com");
        emails.add("pluto@example.com");

        assert (pollCreationService.checkForDuplicateEmail(emails)) == true;
    }

    @Test
    public void removeDuplicateEmailTest(){
        ArrayList<String> emails = new ArrayList<>();
        emails.add("pippo@example.com");
        emails.add("pluto@example.com");
        emails.add("paperino@example.com");
        emails.add("pluto@example.com");

        ArrayList<String> correctEmails = new ArrayList<>();
        correctEmails.add("pippo@example.com");
        correctEmails.add("pluto@example.com");
        correctEmails.add("paperino@example.com");

        assert pollCreationService.removeDuplicateEmail(emails).containsAll(correctEmails);
    }
}
