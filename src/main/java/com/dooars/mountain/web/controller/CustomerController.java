/**
 * 
 */
package com.dooars.mountain.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dooars.mountain.model.common.ApiResult;
import com.dooars.mountain.model.customer.Customer;
import com.dooars.mountain.service.customer.CustomerService;
import com.dooars.mountain.constants.CustomerConstants;





/**
 * @author Prantik Guha
 * 19-May-2021 
 * CustomerController.java
 */

@CrossOrigin
@RestController
@RequestMapping(CustomerConstants.SERVICE_NAME)
public class CustomerController {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);
	
	private final CustomerService service;
	private final Validator validator;
	private ControllerHelper helper;
	
	public CustomerController(CustomerService service, 
			@Qualifier("addCustomerValidator") Validator validator, ControllerHelper helper) {
		this.service = service;
		this.validator = validator;
		this.helper = helper;
	}
	
	
	@SuppressWarnings("unchecked")
	@PostMapping(CustomerConstants.ADD_CUSTOMER_URL)
	public <T> ResponseEntity<T> addCustomer(@RequestBody Customer customer, BindingResult bindingResult) {
		LOGGER.trace("Entering into addCustomer method in CustomerController with{}", customer.toString());
		return (ResponseEntity<T>) helper.validateAndExecute(validator, bindingResult, customer, () -> service.addCustomer(customer));		
	}
}
