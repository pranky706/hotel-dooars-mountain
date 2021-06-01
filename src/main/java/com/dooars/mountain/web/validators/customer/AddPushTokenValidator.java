package com.dooars.mountain.web.validators.customer;


import com.dooars.mountain.web.commands.token.AddPushTokenCommand;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

/**
 * @author Prantik Guha on 29-05-2021
 **/
@Component
public class AddPushTokenValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return AddPushTokenValidator.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AddPushTokenCommand command = (AddPushTokenCommand) target;

        if (Objects.nonNull(command)) {
            if (Objects.isNull(command.getMobileNumber())) {
                errors.rejectValue("MobileNumber", "3", "is required");
            }else if (String.valueOf(command.getMobileNumber()).length() != 10) {
                errors.rejectValue("MobileNumber", "2", "is invalid");
            }
            if (Objects.isNull(command.getPlatform())) {
                errors.rejectValue("Platform", "2", "is required");
            }
            if (Objects.isNull(command.getPushToken())) {
                errors.rejectValue("PushToken", "3", "is required");
            }else if (command.getPushToken().equals("")) {
                errors.rejectValue("PushToken", "2", "is invalid");
            }
        } else {
            errors.rejectValue("Customer Object", "1", "is required");
        }

    }

}