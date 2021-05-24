package com.dooars.mountain.web.validators.customer;


import com.dooars.mountain.model.customer.Location;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

/**
 * @author Prantik Guha on 24-05-2021
 **/
@Component
public class AddLocationValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return AddLocationValidator.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Location command = (Location) target;

        if (Objects.nonNull(command)) {
            if (Objects.isNull(command.getCompleteAddress())) {
                errors.rejectValue("CompleteAddress", "3", "is required");
            }else if (command.getCompleteAddress().equals("")) {
                errors.rejectValue("CompleteAddress", "2", "is required");
            }
            if (Objects.isNull(command.getLatitude())) {
                errors.rejectValue("Latitude", "3", "is required");
            }else if (command.getLatitude().equals("")) {
                errors.rejectValue("Latitude", "2", "is required");
            }
            if (Objects.isNull(command.getLongitude())) {
                errors.rejectValue("Longitude", "3", "is required");
            }else if (command.getLongitude().equals("")) {
                errors.rejectValue("Longitude", "2", "is required");
            }
            if (String.valueOf(command.getPincode()).length() <6) {
                errors.rejectValue("Pincode", "2", "is invalid");
            }
        } else {
            errors.rejectValue("Customer Object", "1", "is required");
        }

    }

}
