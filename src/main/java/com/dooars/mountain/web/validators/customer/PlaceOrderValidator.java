package com.dooars.mountain.web.validators.customer;


import com.dooars.mountain.model.order.Order;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

/**
 * @author Prantik Guha on 27-05-2021
 **/
@Component
public class PlaceOrderValidator  implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PlaceOrderValidator.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Order command = (Order) target;

        if (Objects.nonNull(command)) {
            if (Objects.isNull(command.getLocation())) {
                errors.rejectValue("Location", "3", "is required");
            }
            if (Objects.isNull(command.getMobileNumber())) {
                errors.rejectValue("MobileNumber", "3", "is required");
            }else if (String.valueOf(command.getMobileNumber()).length() != 10) {
                errors.rejectValue("MobileNumber", "2", "is invalid");
            }
            if (Objects.isNull(command.getPaymentMode())) {
                errors.rejectValue("PaymentMode", "3", "is required");
            }
        } else {
            errors.rejectValue("Customer Object", "1", "is required");
        }

    }

}