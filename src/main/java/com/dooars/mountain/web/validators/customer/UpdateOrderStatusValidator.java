package com.dooars.mountain.web.validators.customer;

import com.dooars.mountain.web.commands.order.UpdateOrderStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;

/**
 * @author Prantik Guha on 27-05-2021
 **/
@Component
public class UpdateOrderStatusValidator  implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return UpdateOrderStatusValidator.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UpdateOrderStatus command = (UpdateOrderStatus) target;

        if (Objects.nonNull(command)) {
            if (Objects.isNull(command.getOrderId())) {
                errors.rejectValue("OrderId", "3", "is required");
            }else if (command.getOrderId() == 0) {
                errors.rejectValue("OrderId", "2", "is required");
            }
            if (Objects.isNull(command.getMobileNumber())) {
                errors.rejectValue("MobileNumber", "3", "is required");
            }else if (command.getMobileNumber() == 0 || String.valueOf(command.getMobileNumber()).length() != 10) {
                errors.rejectValue("MobileNumber", "2", "is invalid");
            }
            if (Objects.isNull(command.getCurrentStatus())) {
                errors.rejectValue("CurrentStatus", "3", "is required");
            }
        } else {
            errors.rejectValue("Customer Object", "1", "is required");
        }

    }
}