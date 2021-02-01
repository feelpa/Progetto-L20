package it.unipv.ingsw.l20PollingSystem.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name = "confirmation_token")
public class ConfirmationToken implements Expirable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="TOKEN_ID")
    private int tokenId;

    @Column(name="CONFIRMATION_TOKEN")
    private String confirmationToken;

    @Column(name="EXPIRATION_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime expirationDate;

    @Column(name="USER_ID")
    private int userId;

    public ConfirmationToken(int userId) {
        this.userId = userId;
        this.expirationDate = LocalDateTime.now().plusMinutes(15);
        this.confirmationToken = UUID.randomUUID().toString();
    }
}
