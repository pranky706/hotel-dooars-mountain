/**
 * 
 */
package com.dooars.mountain.service.customer;

import java.util.Map;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.customer.Customer;

/**
 * @author Prantik Guha
 * 19-May-2021 
 * CustomerService.java
 */
public interface CustomerService {
	
	Map<String, Object> addCustomer( Customer customer) throws BaseException;
	Customer getCustomer( long mobileNumber) throws BaseException;

}
