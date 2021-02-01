package it.unipv.ingsw.l20PollingSystem.model;

import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Data
@Entity
public class AllowedUser {

    @EmbeddedId
    private AllowedUserKey id;

}
