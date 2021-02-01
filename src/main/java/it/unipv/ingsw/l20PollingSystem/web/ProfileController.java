package it.unipv.ingsw.l20PollingSystem.web;

import it.unipv.ingsw.l20PollingSystem.data.UserRepository;
import it.unipv.ingsw.l20PollingSystem.model.PollViewer;
import it.unipv.ingsw.l20PollingSystem.model.User;
import it.unipv.ingsw.l20PollingSystem.service.PollViewerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Slf4j
@Controller
@RequestMapping("/user")
public class ProfileController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PollViewerService pollViewerService;

    @GetMapping
    public String redirectToProfile(Authentication auth){
        return "redirect:/user/" + auth.getName();
    }

    @GetMapping(value = "/{username}")
    public String showProfile(@PathVariable String username, Model model){

        User user = userRepo.findByUsername(username);

        //LISTA DEI POLL CREATI DALL'UTENTE
        ArrayList<PollViewer> pollsCreatedByUser = pollViewerService.getPollsCreatedByUser(user.getUserId());

        //LISTA DEI POLL CONDIVISI CON L'UTENTE
        ArrayList<PollViewer> pollsSharedWithUser = pollViewerService.getPollsSharedWithUser(user.getUserId());

        //LISTA DEI POLL PUBBLICI
        ArrayList<PollViewer> publicPolls = pollViewerService.getPublicPolls(user.getUserId());

        model.addAttribute("pollsCreatedByUser", pollsCreatedByUser);
        model.addAttribute("pollsSharedWithUser", pollsSharedWithUser);
        model.addAttribute("publicPolls", publicPolls);

        model.addAttribute("user", user);

        return "profile";
    }

}
