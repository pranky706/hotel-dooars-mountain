/**
 * 
 */
package com.dooars.mountain.service.customer;

import java.util.List;
import java.util.Map;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.customer.Customer;
import com.dooars.mountain.model.customer.Location;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author Prantik Guha
 * 19-May-2021 
 * CustomerService.java
 */
public interface CustomerService {
	
	Map<String, Object> addCustomer( Customer customer) throws BaseException;
	Customer getCustomer( long mobileNumber) throws BaseException;
	Location addLocation( Location location, long mobileNumber) throws BaseException, JsonProcessingException;
	Location updateLocation( Location location, long mobileNumber) throws BaseException, JsonProcessingException;
	List<Location> getLocations(long mobileNumber) throws BaseException;
	Location deleteLocation( long locationId, long mobileNumber) throws BaseException, JsonProcessingException;
}
