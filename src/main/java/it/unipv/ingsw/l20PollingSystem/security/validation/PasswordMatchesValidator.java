package it.unipv.ingsw.l20PollingSystem.security.validation;

import it.unipv.ingsw.l20PollingSystem.security.model.RegistrationForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context){
        RegistrationForm registrationForm = (RegistrationForm) obj;
        return registrationForm.getPassword().equals(registrationForm.getMatchingPassword());
    }
}
