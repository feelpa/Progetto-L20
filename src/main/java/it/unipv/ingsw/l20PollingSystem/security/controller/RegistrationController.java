package it.unipv.ingsw.l20PollingSystem.security.controller;

import it.unipv.ingsw.l20PollingSystem.model.ConfirmationToken;
import it.unipv.ingsw.l20PollingSystem.model.User;
import it.unipv.ingsw.l20PollingSystem.security.model.RegistrationForm;
import it.unipv.ingsw.l20PollingSystem.service.UserService;
import it.unipv.ingsw.l20PollingSystem.security.error.EmailAlreadyExistException;
import it.unipv.ingsw.l20PollingSystem.security.error.UsernameAlreadyExistException;
import it.unipv.ingsw.l20PollingSystem.service.ConfirmationTokenService;
import it.unipv.ingsw.l20PollingSystem.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    ConfirmationTokenService confirmationTokenService;

    @Autowired
    private EmailSenderService emailSenderService;


    @GetMapping
    public String registerForm(Model model) {
        model.addAttribute("form", new RegistrationForm());
        return "sign-up";
    }

    @PostMapping
    public String processRegistration(@ModelAttribute("form") @Valid RegistrationForm form, BindingResult bindingResult, Model model) {

        if(bindingResult.hasErrors()){
            model.addAttribute("form", form);
            return "sign-up";
        }

        try {
            User user = userService.registerNewUser(form);

            ConfirmationToken confirmationToken = new ConfirmationToken(user.getUserId());
            confirmationTokenService.saveConfirmationToken(confirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("Complete registration!");
            mailMessage.setFrom("l20.notification@gmail.com");
            mailMessage.setText("To confirm your account, please click here: " +
                    "http://localhost:9000/register/confirm-account?token=" + confirmationToken.getConfirmationToken());

            emailSenderService.sendEmail(mailMessage);

            String message = "A verification email has been sent to: " + user.getEmail();

            model.addAttribute("message", message);
            return "success";

        } catch (EmailAlreadyExistException eaeEx){
            bindingResult.addError(new FieldError("form", "email", eaeEx.getMessage()));
            model.addAttribute("form", form);
            return "sign-up";
        } catch (UsernameAlreadyExistException aaeEx) {
            bindingResult.addError(new FieldError("form", "username", aaeEx.getMessage()));
            model.addAttribute("form", form);
            return "sign-up";
        }
    }

    @RequestMapping(value="/confirm-account", method= {RequestMethod.GET, RequestMethod.POST})
    public String confirmUserAccount(@RequestParam("token")String confirmationToken, Model model){

        ConfirmationToken token = confirmationTokenService.getConfirmationTokenByToken(confirmationToken);

        if(token != null) {
            if (!(confirmationTokenService.checkExpiration(token))) {

                User user = userService.getUserByUserId(token.getUserId());
                user.setEnabled(true);
                userService.updateUser(user);
                confirmationTokenService.deleteConfirmationToken(token);

                String message = "Congratulations! Your account has been activated and email is verified!";
                model.addAttribute("message", message);
                return "success";
            } else {
                model.addAttribute("message", "The link is invalid or broken!");
                return "failure";
            }
        } else {
            model.addAttribute("message","The link is invalid or broken!");
            return "failure";
        }
    }

}
