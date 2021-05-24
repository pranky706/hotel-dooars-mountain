/**
 * 
 */
package com.dooars.mountain.repository.customer;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.customer.Customer;
import com.dooars.mountain.model.customer.Location;

import java.util.List;

/**
 * @author Prantik Guha
 * 19-May-2021 
 * CustomerRepository.java
 */


public interface CustomerRepository {
	
	void addCustomer(Customer customer) throws BaseException;
	Customer getCustomer(long mobileNumber) throws BaseException;
	void deleteCustomer(long mobileNumber) throws BaseException;
	void updateLocation( String locationsString, long mobileNumber) throws BaseException;
	List<Location> getLocations( long mobileNumber) throws BaseException;
}
