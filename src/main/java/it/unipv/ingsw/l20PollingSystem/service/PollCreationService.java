package it.unipv.ingsw.l20PollingSystem.service;

import it.unipv.ingsw.l20PollingSystem.data.*;
import it.unipv.ingsw.l20PollingSystem.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

@Service
public class PollCreationService extends ExpirationControl{

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PollRepository pollRepo;
    @Autowired
    private QuestionRepository questionRepo;
    @Autowired
    private ChoiceRepository choiceRepo;
    @Autowired
    private AllowedUserRepository allowedUserRepo;
    @Autowired
    private AnswerRepository answerRepo;
    @Autowired
    private EmailSenderService emailSenderService;


    public boolean checkForDuplicateChoice(ArrayList<Choice> choices){
        //non considerare il caso in cui si faccia submit su un form vuoto
        if(choices.size() == 2 &&
                choices.get(0).getCText().isBlank() &&
                choices.get(1).getCText().isBlank()){
            return false;
        }
        HashSet<Choice> test = new HashSet<>(choices);
        return (test.size() < choices.size());
    }

    public ArrayList<Choice> removeDuplicateChoice(ArrayList<Choice> choices) {
        HashSet<Choice> test = new HashSet<>(choices);
        ArrayList<Choice> noDuplicateChoice = new ArrayList<>(test);
        return noDuplicateChoice;
    }

    public boolean checkForDuplicateEmail(ArrayList<String> emails){
        //non considerare il caso in cui si faccia submit su un form vuoto
        if(emails.size() == 2 &&
            emails.get(0).isBlank() &&
                emails.get(1).isBlank()){
            return false;
        }
        HashSet<String> test = new HashSet<>(emails);
        return (test.size() < emails.size());
    }

    public ArrayList<String> removeDuplicateEmail(ArrayList<String> emails){
        HashSet<String> test = new HashSet<>(emails);
        ArrayList<String> noDuplicateEmail = new ArrayList<>(test);
        return noDuplicateEmail;
    }

    public boolean checkForBlankEmail(String email) {
        return (email.trim().length() == 0);
    }

    public boolean checkForNonExistentEmail(String email) {
        return (!(userRepo.existsByEmail(email)));
    }

    public boolean checkForMissingExpirationDate(PollCreationForm form) {
        return (form.getPoll().isDoubleRound() && form.getQuestion().getExpirationDate() == null);
    }

    public void saveNewFormToDatabase(PollCreationForm form, Authentication auth){

        User user = userRepo.findByUsername(auth.getName());
        int creatorId = user.getUserId();

        //save poll
        Poll poll = form.getPoll();
        poll.setCreatorId(creatorId);
        poll.setCreationDate(LocalDateTime.now());
        poll.setOpen(true);
        pollRepo.save(poll);

        //save question
        Question question = form.getQuestion();
        question.setPollId(poll.getPollId());
        question.setOpen(true);
        if (!poll.isDoubleRound()) {
            question.setExpirationDate(poll.getExpirationDate());
        }
        questionRepo.save(question);

        //save choices
        for(Choice choice : form.getChoices()){
            choice.setQuestionId(question.getQuestionId());
            choiceRepo.save(choice);
        }

        //save allowed users
        if(poll.isClassified()) {
            allowedUserRepo.saveAll(generateAllowedUsersListFromEmail(poll, form.getAllowedUsers()));
            sendAlertToAllowedUser(poll.getPollId(), form.getAllowedUsers());
        }

    }

    public PollCreationForm retrievePollForEditing(int pollId){
        Poll p = pollRepo.findByPollId(pollId);
        Question q = questionRepo.findByPollIdAndIsOpen(pollId, true);
        ArrayList<Choice> c = choiceRepo.findAllByQuestionId(q.getQuestionId());
        ArrayList<String> emails = new ArrayList<>(1);
        if(p.isClassified()){
            ArrayList<AllowedUser> au = allowedUserRepo.findAllByIdPollId(pollId);
            emails = generateEmailListFromAllowedUsers(au);
        }
        return new PollCreationForm(p, q, c, emails);
    }

    public void updatePoll(PollCreationForm form){
        //save poll
        Poll p = pollRepo.save(form.getPoll());

        //save question
        Question q = questionRepo.save(form.getQuestion());

        //save choices
        for(Choice choice : form.getChoices()){
            if(choice.getQuestionId() == 0){
                choice.setQuestionId(q.getQuestionId());
            }
        }
        ArrayList<Choice> c = (ArrayList<Choice>) choiceRepo.saveAll(form.getChoices());

        //save allowed users
        if(form.getPoll().isClassified() & !(form.getAllowedUsers().isEmpty())) {
            allowedUserRepo.saveAll(generateAllowedUsersListFromEmail(form.getPoll(), form.getAllowedUsers()));
        }
    }

    public void deletePoll(Poll poll){
        if(poll.isClassified()) {
            ArrayList<AllowedUser> allowedUsers = allowedUserRepo.findAllByIdPollId(poll.getPollId());
            allowedUserRepo.deleteAll(allowedUsers);
        }
        ArrayList<Answer> answers = answerRepo.findAllByPollId(poll.getPollId());
        answerRepo.deleteAll(answers);
        ArrayList<Question> questions = questionRepo.findAllByPollId(poll.getPollId());
        ArrayList<Choice> choices;
        for(Question q : questions){
            choices = choiceRepo.findAllByQuestionId(q.getQuestionId());
            choiceRepo.deleteAll(choices);
        }
        questionRepo.deleteAll(questions);
        pollRepo.delete(poll);
    }

    public PollCreationForm checkForDifferencesInForms(PollCreationForm previousForm, PollCreationForm currentForm) {

        //differences in polls
        currentForm.getPoll().setCreationDate(LocalDateTime.now());

        if(previousForm.getPoll().isDoubleRound() & !(currentForm.getPoll().isDoubleRound()))
            currentForm.getQuestion().setExpirationDate(currentForm.getPoll().getExpirationDate());

        if(previousForm.getPoll().isClassified() & !(currentForm.getPoll().isClassified()))
            currentForm.setAllowedUsers(new ArrayList<String>(1));

        //differences in choices
        for(Choice previousChoice : previousForm.getChoices()){
            if(!(currentForm.getChoices().contains(previousChoice))){
                choiceRepo.delete(previousChoice);
            }
        }

        //differences in emails
        for(String email : previousForm.getAllowedUsers()){
            if(!(currentForm.getAllowedUsers().contains(email))){
                int userId = userRepo.findByEmailIgnoreCase(email).getUserId();

                AllowedUserKey keyToUserToRemove = new AllowedUserKey();
                keyToUserToRemove.setUserId(userId);
                keyToUserToRemove.setPollId(currentForm.getPoll().getPollId());
                Optional<AllowedUser> optionalUserToRemove = allowedUserRepo.findById(keyToUserToRemove);

                if(optionalUserToRemove.isPresent()){
                    AllowedUser userToRemove = optionalUserToRemove.get();
                    allowedUserRepo.delete(userToRemove);
                }
            }
        }
        return currentForm;
    }

    private ArrayList<AllowedUser> generateAllowedUsersListFromEmail(Poll poll, ArrayList<String> emails){
        ArrayList<AllowedUser> allowedUsers = new ArrayList<>();

        for (String email : emails){
            User user = userRepo.findByEmailIgnoreCase(email);

            AllowedUserKey allowedUserKey = new AllowedUserKey();
            allowedUserKey.setPollId(poll.getPollId());
            allowedUserKey.setUserId(user.getUserId());

            AllowedUser allowedUser = new AllowedUser();
            allowedUser.setId(allowedUserKey);

            allowedUsers.add(allowedUser);
        }

        return allowedUsers;
    }

    private ArrayList<String> generateEmailListFromAllowedUsers(ArrayList<AllowedUser> allowedUsers){
        ArrayList<String> emails = new ArrayList<>();

        for (AllowedUser allowedUser : allowedUsers){
            String email = userRepo.findByUserId(allowedUser.getId().getUserId()).getEmail();
            emails.add(email);
        }

        return emails;
    }

    private void sendAlertToAllowedUser(int pollId, ArrayList<String> emails){

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setSubject("A new poll is waiting for you!");
        mailMessage.setFrom("l20.notification@gmail.com");
        mailMessage.setText("You've been invited to a new poll.\nTo check it out please click here: "
                + "http://localhost:9000/poll/" + pollId);

        for(String email : emails) {
            mailMessage.setTo(email);
            emailSenderService.sendEmail(mailMessage);
        }
    }

    public void closeExpiredPoll(Poll poll){
        if(checkExpiration(poll)) {
            poll.setOpen(false);
            pollRepo.save(poll);
        }
    }

    public PollCreationForm setUpSecondRound(Poll poll, Question firstRoundQuestion){
        Question secondRoundQuestion = generateQuestionForSecondRound(poll, firstRoundQuestion);
        ArrayList<Choice> secondRoundChoices = selectChoicesForSecondRound(firstRoundQuestion, secondRoundQuestion);
        return new PollCreationForm(poll, secondRoundQuestion, secondRoundChoices);
    }

    private Question generateQuestionForSecondRound(Poll poll, Question firstRoundQuestion){

        //chiudo la domanda scaduta e l'aggiorno nel db
        firstRoundQuestion.setOpen(false);
        questionRepo.save(firstRoundQuestion);

        //creo una nuova domanda associata allo stesso poll e la salvo nel db
        Question secondRoundQuestion = new Question();
        secondRoundQuestion.setPollId(poll.getPollId());
        secondRoundQuestion.setExpirationDate(poll.getExpirationDate());
        secondRoundQuestion.setOpen(true);
        secondRoundQuestion.setQText("[SECOND ROUND] " + firstRoundQuestion.getQText());

        return secondRoundQuestion;
    }

    private ArrayList<Choice> selectChoicesForSecondRound(Question firstRoundQuestion, Question secondRoundQuestion){

        //genero una lista di interi che rappresentano i voti presi da ciascuna opzione
        //a partire dal result set
        ArrayList<ResultSet> resultSets = answerRepo.getResult(firstRoundQuestion.getQuestionId());
        ArrayList<Integer> votes = new ArrayList<>();
        for(ResultSet rs : resultSets) {
            votes.add(rs.getCount());
        }
        votes.trimToSize();


        //trasformo l'arraylist in un set (che non ammette duplicati),
        //lo ritrasformo in un arraylist e lo ordino dal maggiore al minore
        HashSet<Integer> votesSet = new HashSet<>(votes);
        ArrayList<Integer> noDuplicateVotes = new ArrayList<>(votesSet);
        Collections.sort(noDuplicateVotes, Collections.reverseOrder());


        //itero l'arraylist dei risultati e se i voti presi da una certa opzione
        //sono uguali al primo elemento dell'array dei voti senza duplicati ordinati dal maggiore al minore
        //vado a prendere nel db l'opzione e l'aggiungo all'array delle opzioni per il ballottaggio
        ArrayList<Choice> choices = new ArrayList<>();
        for(ResultSet rs : resultSets){
            if(rs.getCount() == noDuplicateVotes.get(0)) {
                Choice c = new Choice();
                c.setCText(rs.getCText());
                choices.add(c);
            }
        }
        choices.trimToSize();


        //se nell'iterazione precedente è stata recuperata un'opzione sola effettuo una nuova iterazione
        //andando a confrontare questa volta i voti delle opzioni nel resultset con il secondo elemento
        //dell'array dei voti senza duplicati e ordinati dal maggiore al minore
        if(!(choices.size() > 1)){
            for(ResultSet rs : resultSets){
                if(rs.getCount() == noDuplicateVotes.get(1)) {
                    Choice c = new Choice();
                    c.setCText(rs.getCText());
                    choices.add(c);
                }
            }
        }

        //a questo punto dovrei aver preso tutte le opzioni che hanno preso il maggior numero di voti.
        //potrebbero essere due o di più, ma non possiamo far scegliere al computer quali eliminare.
        return choices;
    }
}
