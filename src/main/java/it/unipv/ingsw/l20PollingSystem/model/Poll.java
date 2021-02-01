package it.unipv.ingsw.l20PollingSystem.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Entity
public class Poll implements Expirable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pollId;

    private int creatorId;

    private LocalDateTime creationDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NotNull(message = "Please select an expiration date for your poll.")
    private LocalDateTime expirationDate;

    private boolean doubleRound;

    private boolean classified;

    @Column(name = "ISOPEN")
    private boolean isOpen;

}
