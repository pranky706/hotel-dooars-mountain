package com.dooars.mountain.web.validators.offer;

import com.dooars.mountain.model.centraloffer.CentralOffer;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

/**
 * @author Prantik Guha on 23-05-2021
 **/

@Component
public class UpdateOfferValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return UpdateOfferValidator.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CentralOffer command = (CentralOffer) target;

        if (Objects.nonNull(command)) {
            if (Objects.isNull(command.getOfferImageName())) {
                errors.rejectValue("OfferImageName", "3", "is required");
            }else if (command.getOfferImageName().equals("")) {
                errors.rejectValue("OfferImageName", "2", "is required");
            }
            if (Objects.isNull(command.getOffer())) {
                errors.rejectValue("Offer", "3", "value is required");
            }else if (command.getOffer() == 0) {
                errors.rejectValue("Offer", "2", "value is required");
            }
            if (Objects.isNull(command.getOfferFrom())) {
                errors.rejectValue("OfferFrom", "3", "is required");
            }
            if (Objects.isNull(command.getOfferUpto())) {
                errors.rejectValue("OfferUpto", "3", "is required");
            }
            if (Objects.isNull(command.getOfferId()) || 0 == command.getOfferId()) {
                errors.rejectValue("OfferId", "3", "is required");
            }
        } else {
            errors.rejectValue("Customer Object", "1", "is required");
        }

    }
}