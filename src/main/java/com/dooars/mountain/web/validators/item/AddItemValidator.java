/**
 * 
 */
package com.dooars.mountain.web.validators.item;

import java.util.Objects;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.dooars.mountain.model.item.Item;


/**
 * @author Prantik Guha
 * 21-May-2021 
 * AddItemValidator.java
 */

@Component
public class AddItemValidator  implements Validator {
	@Override
	public boolean supports(Class<?> clazz) {
		return AddItemValidator.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Item command = (Item) target;
		
		if (Objects.nonNull(command)) {
			if (Objects.isNull(command.getItemName())) {
				errors.rejectValue("ItemName", "3", "is required");
			}else if (command.getItemName().equals("")) {
				errors.rejectValue("ItemName", "2", "is required");
			}
			if (Objects.isNull(command.getImageName())) {
				errors.rejectValue("ImageName", "3", "is required");
			}else if (command.getImageName().equals("")) {
				errors.rejectValue("ImageName", "2", "is required");
			}
			if (Objects.isNull(command.getGroupId()) || 0 == command.getGroupId()) {
				errors.rejectValue("GroupId", "3", "is required");
			}
		} else {
			errors.rejectValue("Customer Object", "1", "is required");
		}
		
	}

}
