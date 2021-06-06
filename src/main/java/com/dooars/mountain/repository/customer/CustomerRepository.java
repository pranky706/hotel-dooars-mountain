/**
 * 
 */
package com.dooars.mountain.repository.customer;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.customer.Customer;
import com.dooars.mountain.model.customer.CustomerToken;
import com.dooars.mountain.model.customer.Location;
import com.dooars.mountain.model.order.CurrentStatus;
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
	long getOrderCount() throws BaseException;
	long getOrderCountNotCompleted() throws BaseException;
	long getOrderCount(CurrentStatus currentStatus) throws BaseException;
	void updateLocation(String locationString, long mobileNumber, long locationId) throws BaseException;
	void addLocation( String locationString, long mobileNumber, long locationId) throws BaseException;
	void deleteLocation(long locationId, long mobileNumber) throws BaseException;
	List<Location> getLocations( long mobileNumber) throws BaseException;
	List<Order> getOrders(long mobileNumber) throws BaseException;
	Order getOrderByIdAndNumber(long mobileNumber, long orderId) throws BaseException;
	Order getOrderById(long orderId) throws BaseException;
	Location getLocationByIdAndNumber(long mobileNumber, long locationId) throws BaseException;
	List<Order> getAllOrders(int noOfObjects, int currentPage) throws BaseException;
	List<Order> getAllOrdersNotCompleted(int noOfObjects, int currentPage) throws BaseException;
	List<Order> getAllOrders(CurrentStatus currentStatus, int noOfObjects, int currentPage) throws BaseException;
	void updateOrder(String orderString, long mobileNumber, long orderId) throws BaseException;
	void addOrder( String orderString, long mobileNumber, long orderId) throws BaseException;
	void deleteOrder(long orderId, long mobileNumber) throws BaseException;
	void updatePushToken(String tokensString, long mobileNumber) throws BaseException;
	List<CustomerToken> getCustomerTokens(long mobileNumber) throws BaseException;
	List<Order> getDailyOrders(long start, long end) throws BaseException;
}
