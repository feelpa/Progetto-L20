package it.unipv.ingsw.l20PollingSystem.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Entity
public class Question implements Expirable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int questionId;

    private int pollId;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime expirationDate;

    private boolean isOpen;

    @Column(name = "QTEXT")
    @NotBlank(message = "You must provide a question.")
    private String qText;

}
