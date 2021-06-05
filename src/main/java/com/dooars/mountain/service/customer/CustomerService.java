/**
 * 
 */
package com.dooars.mountain.service.customer;

import java.util.List;
import java.util.Map;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.customer.Customer;
import com.dooars.mountain.model.customer.CustomerToken;
import com.dooars.mountain.model.customer.Location;
import com.dooars.mountain.model.order.CurrentStatus;
import com.dooars.mountain.model.order.Order;
import com.dooars.mountain.web.commands.token.AddPushTokenCommand;
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
	Order deleteOrder( long orderId, long mobileNumber) throws BaseException, JsonProcessingException;
	Order addOrder(Order order, long mobileNumber) throws BaseException, JsonProcessingException;
	List<Order> getOrders(long mobileNumber) throws BaseException;
	List<Order> getAllOrders(int noOfObjects, int currentPage) throws BaseException;
	List<Order> getAllOrdersNotCompleted(int noOfObjects, int currentPage) throws BaseException;
	List<Order> getAllOrdersByStatus(CurrentStatus currentStatus, int noOfObjects, int currentPage) throws BaseException;
	Order updateOrderStatus(long orderId, long mobileNumber, CurrentStatus currentStatus) throws BaseException, JsonProcessingException;
	void addPushToken(AddPushTokenCommand command) throws BaseException, JsonProcessingException;
	List<CustomerToken> getTokens(long mobileNumber) throws BaseException;
	long getOrderCount() throws BaseException;
	long getOrderCountNotCompleted() throws BaseException;
	long getOrderCount(CurrentStatus currentStatus) throws BaseException;
}
