package it.unipv.ingsw.l20PollingSystem.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Entity
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int answerId;

    private int pollId;

    private int questionId;

    private int userId;

    @Min(value = 1, message = "Select an option")
    private int choiceId;

    private LocalDateTime castingTime;

}
