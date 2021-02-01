package it.unipv.ingsw.l20PollingSystem.security.controller;

import it.unipv.ingsw.l20PollingSystem.model.ConfirmationToken;
import it.unipv.ingsw.l20PollingSystem.model.User;
import it.unipv.ingsw.l20PollingSystem.security.model.RegistrationForm;
import it.unipv.ingsw.l20PollingSystem.service.UserService;
import it.unipv.ingsw.l20PollingSystem.service.ConfirmationTokenService;
import it.unipv.ingsw.l20PollingSystem.service.EmailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@Controller
@SessionAttributes("user")
public class PasswordOperationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ConfirmationTokenService confirmationTokenService;

    @Autowired
    private EmailSenderService emailSenderService;



    // FORGOT PASSWORD

    @GetMapping(value = "/forgot-password")
    public String displayResetPassword(Model model) {
        model.addAttribute("user", new User());
        return "forgot-password";
    }

    // Receive the address and send an email
    @PostMapping(value = "/forgot-password")
    public String forgotUserPassword(@ModelAttribute("user") User user, Model model) {
        User existingUser = userService.getUserByEmail(user.getEmail());
        if (existingUser != null) {
            // Create token
            ConfirmationToken token = new ConfirmationToken(existingUser.getUserId());

            // Save it
            confirmationTokenService.saveConfirmationToken(token);

            // Create the email
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(existingUser.getEmail());
            mailMessage.setSubject("Complete Password Reset!");
            mailMessage.setFrom("l20.notification@gmail.com");
            mailMessage.setText("To complete the password reset process, please click here: "
                    + "http://localhost:9000/confirm-reset?token="+token.getConfirmationToken());

            // Send the email
            emailSenderService.sendEmail(mailMessage);

            model.addAttribute("message", "Request to reset password received. Check your inbox for the reset link.");
            return "success";
        } else {
            model.addAttribute("message", "This email address does not exist!");
            return "failure";
        }
    }

    // Endpoint to confirm the token
    @GetMapping(value="/confirm-reset")
    @PostMapping(value="/confirm-reset")
    public String validateResetToken(@RequestParam("token")String confirmationToken, Model model) {
        ConfirmationToken token = confirmationTokenService.getConfirmationTokenByToken(confirmationToken);

        if (token != null) {
            if(!confirmationTokenService.checkExpiration(token)) {
                User user = userService.getUserByUserId(token.getUserId());
                confirmationTokenService.deleteConfirmationToken(token);

                RegistrationForm form = new RegistrationForm();
                form.setUsername(user.getUsername());
                form.setEmail(user.getEmail());

                model.addAttribute("user", form);
                return "reset-password";
            } else {
                model.addAttribute("message", "The link is invalid or broken!");
                return "failure";
            }
        }  else {
            model.addAttribute("message", "The link is invalid or broken!");
            return "failure";
        }
    }

    // Endpoint to update a user's password
    @PostMapping(value = "/reset-password")
    public String resetUserPassword(@ModelAttribute("user") @Valid RegistrationForm form, Errors errors, Model model) {
        if(errors.hasErrors()){
            model.addAttribute("user", form);
            return "reset-password";
        } else if (form.getEmail() != null) {
            // Use email to find user
            User tokenUser = userService.getUserByEmail(form.getEmail());
            String password = userService.encodePassword(form.getPassword());
            tokenUser.setPassword(password);
            userService.updateUser(tokenUser);
            model.addAttribute("message", "Password successfully reset. You can now log in with the new credentials.");
            return "success";
        } else {
            model.addAttribute("message","The link is invalid or broken!");
            return "failure";
        }
    }



    //UPDATE PASSWORD

    @GetMapping(value = "/user/{username}/update-password")
    public String displayUpdatePassword(@PathVariable("username") String username, Model model){
        User user = userService.getUserByUsername(username);
        RegistrationForm form = new RegistrationForm();
        form.setUsername(user.getUsername());
        form.setEmail(user.getEmail());
        model.addAttribute("user", form);
        return "update-password";
    }

    @PostMapping(value = "/user/{username}/update-password")
    public String updatePassword(@ModelAttribute("user") @Valid RegistrationForm form, Errors errors, Model model){
        if(errors.hasErrors()){
            model.addAttribute("user", form);
            return "update-password";
        } else {
            User user = userService.getUserByEmail(form.getEmail());
            String password = userService.encodePassword(form.getPassword());
            user.setPassword(password);
            userService.updateUser(user);
            model.addAttribute("message", "Password successfully reset.");
            return "success";
        }
    }
}
