package com.dooars.mountain.web.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import com.dooars.mountain.model.common.ApiResult;
import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.common.Error;




@Component
public class ControllerHelper {
	
	private final BindingResultErrorCollector errorCollector;
	
	@Autowired
	public ControllerHelper(BindingResultErrorCollector errorCollector) {
		this.errorCollector = errorCollector;
	}
	
	public <T, R> ResponseEntity<T> validateAndExecute(Validator validator, BindingResult bindingResult, R command, ServiceExecutor<T> executor) {
		validator.validate(command, bindingResult);
		if (bindingResult.hasErrors()) {
			return constructFieldErrorResponse(bindingResult);
		}
		return execute(executor);
	}
	
	public <T> ResponseEntity<T> execute(ServiceExecutor<T> executor) {
		try {
			T payload = executor.execute();
			return constructSuccessResponse(payload);
		} catch (BaseException exp) {
			return constructErrorResponse(exp);
		} catch (Exception exp) {
			return constructErrorResponse(exp);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> ResponseEntity<T> constructFieldErrorResponse(BindingResult bindingResult) {
		return new ResponseEntity<T>((T) ApiResult.newInstance().withFiledErrors(errorCollector.getAllErrors(bindingResult)).build(), HttpStatus.BAD_REQUEST);
	}
	
	public <T> ResponseEntity<T> constructNotFoundResponse() {
		return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
	}
	
	@SuppressWarnings("unchecked")
	public <T> ResponseEntity<T> constructSuccessResponse(T payloaad) {
		if (null == payloaad) {
			return new ResponseEntity<T>(HttpStatus.CONFLICT);
		}
		return new ResponseEntity<T>((T) ApiResult.newInstance().withPayload(payloaad).build(), HttpStatus.OK);
	}
	
	@SuppressWarnings("unchecked")
	public <T> ResponseEntity<T> constructErrorResponse(BaseException exp) {
		Error error = Error.createInstance(exp.getMessage(), exp.getLayer());
		return new ResponseEntity<T>((T) ApiResult.newInstance().withErrors(Collections.singletonList(error)).build(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@SuppressWarnings("unchecked")
	public <T> ResponseEntity<T> constructErrorResponse(Exception exp) {
		Error error = Error.createInstance(exp.getMessage(), "");
		return new ResponseEntity<T>((T) ApiResult.newInstance().withErrors(Collections.singletonList(error)).build(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
