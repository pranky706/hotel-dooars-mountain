package com.dooars.mountain.web.controller;

import java.util.List;
import java.util.stream.Collectors;


import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import com.dooars.mountain.model.common.FieldError;





@Component
public class BindingResultErrorCollector {
	
	public List<FieldError> getAllErrors(BindingResult bindingResult) {
		List<FieldError> list1 = getFieldErrors(bindingResult);
		List<FieldError> list2 = getGlobalErrors(bindingResult);
		list1.addAll(list2);
		return list1;
	}
	
	public List<FieldError> getFieldErrors(BindingResult bindingResult) {
		return bindingResult.getFieldErrors().stream().map(error -> getFieldError(error)).collect(Collectors.toList());
	}
	
	public List<FieldError> getGlobalErrors(BindingResult bindingResult) {
		return bindingResult.getGlobalErrors().stream().map(error -> getGlobalError(error)).collect(Collectors.toList());
	}
	
	private FieldError getFieldError(org.springframework.validation.FieldError error) {
		return FieldError.createInstance(
				error.getField(), 
				error.getField() + " " + error.getDefaultMessage()
		);
	}
	
	private FieldError getGlobalError(ObjectError error) {
		return FieldError.createInstance(
				error.getObjectName(), 
				error.getCode() + " " + error.getDefaultMessage()
		);
	}
}
