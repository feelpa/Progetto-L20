package it.unipv.ingsw.l20PollingSystem.security.model;

import it.unipv.ingsw.l20PollingSystem.model.User;
import it.unipv.ingsw.l20PollingSystem.security.validation.PasswordMatches;
import it.unipv.ingsw.l20PollingSystem.security.validation.ValidEmail;
import it.unipv.ingsw.l20PollingSystem.security.validation.ValidPassword;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotBlank;

@Data
@PasswordMatches
public class RegistrationForm {

    @NotBlank(message = "Username must not be blank")
    private String username;

    @ValidEmail
    private String email;

    @NotBlank(message = "Password must not be blank")
    @ValidPassword
    private String password;
    private String matchingPassword;

    public User toUser(PasswordEncoder passwordEncoder){
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        return user;
    }
}
