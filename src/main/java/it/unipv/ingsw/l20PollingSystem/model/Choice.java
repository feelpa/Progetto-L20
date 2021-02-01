package it.unipv.ingsw.l20PollingSystem.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
@Entity
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int choiceId;

    private int questionId;

    @Column(name = "CTEXT")
    @NotBlank(message = "Options cannot be blank.")
    private String cText;

}
