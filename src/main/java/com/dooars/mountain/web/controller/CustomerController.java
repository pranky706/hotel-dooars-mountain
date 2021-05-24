/**
 * 
 */
package com.dooars.mountain.web.controller;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.customer.Location;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import com.dooars.mountain.model.common.ApiResult;
import com.dooars.mountain.model.customer.Customer;
import com.dooars.mountain.service.customer.CustomerService;
import com.dooars.mountain.constants.CustomerConstants;

import java.util.Collections;
import java.util.List;


/**
 * @author Prantik Guha
 * 19-May-2021 
 * CustomerController.java
 */

@CrossOrigin
@RestController
public class CustomerController {
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerController.class);
	
	private final CustomerService service;
	private final Validator validator, addLocationValidator, updateLocationValidator;
	private ControllerHelper helper;
	
	public CustomerController(CustomerService service, @Qualifier("addCustomerValidator") Validator validator,
							  @Qualifier("addLocationValidator") Validator addLocationValidator,
							  @Qualifier("updateLocationValidator") Validator updateLocationValidator,
							  ControllerHelper helper) {
		this.service = service;
		this.validator = validator;
		this.addLocationValidator = addLocationValidator;
		this.updateLocationValidator = updateLocationValidator;
		this.helper = helper;
	}
	
	
	@SuppressWarnings("unchecked")
	@PostMapping(CustomerConstants.ADD_CUSTOMER_URL)
	public <T> ResponseEntity<T> addCustomer(@RequestBody Customer customer, BindingResult bindingResult) {
		LOGGER.trace("Entering into addCustomer method in CustomerController with{}", customer.toString());
		return (ResponseEntity<T>) helper.validateAndExecute(validator, bindingResult, customer, () -> service.addCustomer(customer));		
	}

	@PostMapping(CustomerConstants.GET_CUSTOMER_URL)
	public <T> ResponseEntity<T> getCustomer(@RequestParam("mobileNumber") long mobileNumber) {
		LOGGER.trace("Entering into addCustomer method in CustomerController with {}", mobileNumber);
		try {
			Customer customer = service.getCustomer(mobileNumber);
			if ( null != customer) {
				return new ResponseEntity<T>((T) customer, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
			}

		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}

	@PostMapping(CustomerConstants.ADD_LOCATION_URL)
	public <T> ResponseEntity<T> addLocation(@RequestBody Location location, @RequestParam("mobileNumber") long mobileNumber, BindingResult bindingResult) {
		LOGGER.trace("Entering into addLocation method in CustomerController with {}", location);
		try {
			addLocationValidator.validate(location, bindingResult);
			if (bindingResult.hasErrors())
				return helper.constructFieldErrorResponse(bindingResult);
			Location addedLocation = service.addLocation(location, mobileNumber);
			if ( null != addedLocation) {
				return new ResponseEntity<T>((T) addedLocation, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
			}

		} catch (BaseException | JsonProcessingException e) {
			return helper.constructErrorResponse(e);
		}
	}

	@PostMapping(CustomerConstants.GET_LOCATION_URL)
	public <T> ResponseEntity<T> getLocation(@RequestParam("mobileNumber") long mobileNumber) {
		LOGGER.trace("Entering into getLocation method in CustomerController with {}", mobileNumber);
		try {
			List<Location> locations = service.getLocations(mobileNumber);
			if ( null != locations && locations.size() > 0) {
				return new ResponseEntity<T>((T) locations, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>((T) Collections.emptyList(), HttpStatus.NOT_FOUND);
			}

		} catch (BaseException e) {
			return helper.constructErrorResponse(e);
		}
	}

	@PostMapping(CustomerConstants.UPDATE_LOCATION_URL)
	public <T> ResponseEntity<T> updateLocation(@RequestBody Location location, @RequestParam("mobileNumber") long mobileNumber, BindingResult bindingResult) {
		LOGGER.trace("Entering into updateLocation method in CustomerController with {}", mobileNumber);
		try {
			updateLocationValidator.validate(location, bindingResult);
			if (bindingResult.hasErrors())
				return helper.constructFieldErrorResponse(bindingResult);
			Location updatedLocation = service.updateLocation(location, mobileNumber);
			if ( null != updatedLocation) {
				return new ResponseEntity<T>((T) updatedLocation, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
			}

		} catch (BaseException | JsonProcessingException e) {
			return helper.constructErrorResponse(e);
		}
	}

	@PostMapping(CustomerConstants.DELETE_LOCATION_URL)
	public <T> ResponseEntity<T> deleteLocation(@RequestParam("locationId") long locationId, @RequestParam("mobileNumber") long mobileNumber) {
		LOGGER.trace("Entering into deleteLocation method in CustomerController with {} {}", mobileNumber, locationId);
		try {
			if ( String.valueOf(mobileNumber).length() != 10)
				return new ResponseEntity<T>(HttpStatus.BAD_REQUEST);
			Location deletedLocation = service.deleteLocation(locationId, mobileNumber);
			if ( null != deletedLocation) {
				return new ResponseEntity<T>((T) deletedLocation, HttpStatus.OK);
			} else {
				return new ResponseEntity<T>(HttpStatus.NOT_FOUND);
			}

		} catch (BaseException | JsonProcessingException e) {
			return helper.constructErrorResponse(e);
		}
	}


}
