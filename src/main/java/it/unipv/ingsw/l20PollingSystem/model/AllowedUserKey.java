package it.unipv.ingsw.l20PollingSystem.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@RequiredArgsConstructor
@Embeddable
public class AllowedUserKey implements Serializable {

    @Column(name = "POLL_ID", nullable = false)
    private int pollId;

    @Column(name = "USER_ID", nullable = false)
    private int userId;

}
