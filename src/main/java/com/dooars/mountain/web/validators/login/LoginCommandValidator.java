package com.dooars.mountain.web.validators.login;

import com.dooars.mountain.web.commands.login.LoginCommand;
import com.dooars.mountain.web.validators.customer.AddCustomerValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

/**
 * @author Prantik Guha on 02-06-2021
 **/
@Component
public class LoginCommandValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return LoginCommandValidator.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LoginCommand command = (LoginCommand) target;

        if (Objects.nonNull(command)) {
            if (Objects.isNull(command.getPassword())) {
                errors.rejectValue("Password", "3", "is required");
            }else if (command.getPassword().equals("")) {
                errors.rejectValue("Password", "2", "is required");
            }
            if (String.valueOf(command.getMobileNumber()).length() != 10) {
                errors.rejectValue("MobileNumber", "2", "is not of length 10");
            }
        } else {
            errors.rejectValue("Customer Object", "1", "is required");
        }

    }

}