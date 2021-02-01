package it.unipv.ingsw.l20PollingSystem.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
public class PollViewer {

    private Poll poll;
    private Question question;
    private ArrayList<Choice> choices;

    public PollViewer(Poll p, Question q, ArrayList<Choice> c){
        this.poll = p;
        this.question = q;
        this.choices = c;
    }
}
