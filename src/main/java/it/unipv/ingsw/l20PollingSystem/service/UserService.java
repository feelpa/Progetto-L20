package it.unipv.ingsw.l20PollingSystem.service;

import it.unipv.ingsw.l20PollingSystem.data.UserRepository;
import it.unipv.ingsw.l20PollingSystem.model.User;
import it.unipv.ingsw.l20PollingSystem.security.error.EmailAlreadyExistException;
import it.unipv.ingsw.l20PollingSystem.security.error.UsernameAlreadyExistException;
import it.unipv.ingsw.l20PollingSystem.security.model.RegistrationForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserService {

    @Autowired
    UserRepository userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;


    public User registerNewUser(RegistrationForm form) throws EmailAlreadyExistException, UsernameAlreadyExistException {
        User user = form.toUser(passwordEncoder);
        user.setEnabled(false);
        if (emailExist(user.getEmail())) {
            throw new EmailAlreadyExistException();
        } else if (usernameExist(user.getUsername())) {
            throw new UsernameAlreadyExistException();
        }
        return userRepo.save(user);
    }

    private boolean emailExist(String email) {
        return userRepo.existsByEmail(email);
    }

    private boolean usernameExist(String username) {
        return userRepo.existsByUsername(username);
    }

    public User getUserByUserId(int userId){
        return userRepo.findByUserId(userId);
    }

    public User getUserByUsername(String username){
        return userRepo.findByUsername(username);
    }

    public User getUserByEmail(String email){
        return userRepo.findByEmailIgnoreCase(email);
    }

    public String encodePassword(String password){
        return passwordEncoder.encode(password);
    }

    public User updateUser(User user){
        return userRepo.save(user);
    }

}
