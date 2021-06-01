/**
 * 
 */
package com.dooars.mountain.repository.customer;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.customer.Customer;
import com.dooars.mountain.model.customer.CustomerToken;
import com.dooars.mountain.model.customer.Location;
import com.dooars.mountain.model.order.Order;

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
	List<Order> getOrders(long mobileNumber) throws BaseException;
	void updateOrder(String ordersString, long mobileNumber) throws BaseException;
	void updatePushToken(String tokensString, long mobileNumber) throws BaseException;
	List<CustomerToken> getCustomerTokens(long mobileNumber) throws BaseException;
	void updateOTP(int otp, long mobileNumber) throws BaseException;
	int getOtp(long mobileNumber) throws BaseException;
}
