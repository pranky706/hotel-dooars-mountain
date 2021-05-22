/**
 * 
 */
package com.dooars.mountain.web.validators.customer;

import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.dooars.mountain.model.customer.Customer;

/**
 * @author Prantik Guha
 * 18-May-2021 
 * AddCustomerValidator.java
 */

@Component
public class AddCustomerValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return AddCustomerValidator.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Customer command = (Customer) target;
		
		if (Objects.nonNull(command)) {
			if (Objects.isNull(command.getCustName())) {
				errors.rejectValue("CustName", "3", "is required");
			}else if (command.getCustName().equals("")) {
				errors.rejectValue("CustName", "2", "is required");
			}
			if (String.valueOf(command.getMobileNumber()).length() != 10) {
				errors.rejectValue("MobileNumber", "2", "is not of length 10");
			}
		} else {
			errors.rejectValue("Customer Object", "1", "is required");
		}
		
	}

}
