/**
 * 
 */
package com.dooars.mountain.service.customer;

import java.security.SignatureException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.customer.Customer;
import com.dooars.mountain.model.customer.CustomerToken;
import com.dooars.mountain.model.customer.Location;
import com.dooars.mountain.model.customer.Platform;
import com.dooars.mountain.model.deliveryboy.DeliveryBoy;
import com.dooars.mountain.model.operation.OperationTime;
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
	boolean verifySign(Order order, long mobileNumber) throws BaseException, SignatureException;
	double getDeliveryCharge(double lat, double lon);
	Order getOrderByOrderId(long orderId) throws BaseException;
	List<Order> getOrders(long mobileNumber) throws BaseException;
	List<Order> getAllOrders(int noOfObjects, int currentPage) throws BaseException;
	List<Order> getAllOrdersNotCompleted(int noOfObjects, int currentPage) throws BaseException;
	List<Order> getAllOrdersByStatus(CurrentStatus currentStatus, int noOfObjects, int currentPage) throws BaseException;
	Order updateOrderStatus(long orderId, long mobileNumber, CurrentStatus currentStatus) throws BaseException, JsonProcessingException;
	void addPushToken(AddPushTokenCommand command) throws BaseException, JsonProcessingException;
	List<CustomerToken> getTokens(long mobileNumber) throws BaseException;
	long getOrderCount() throws BaseException;
	long getOrderCountNotCompleted() throws BaseException;
	Order addDeliveryBoyToOrder(DeliveryBoy deliveryBoy, long  orderId) throws BaseException, JsonProcessingException;
	long getOrderCount(CurrentStatus currentStatus) throws BaseException;
	void removePushToken(long mobileNumber, Platform platform) throws BaseException, JsonProcessingException;
	Map<String, Object> getDailySell(LocalDate date) throws BaseException;
	Map<String, Object> getMonthlySell(int year, int month) throws BaseException;
	List<Map<String, Object>> getItemWiseDailySell(LocalDate date) throws BaseException;
	List<Map<String, Object>> getItemWiseMonthlySell(int year, int month) throws BaseException;
	void sendPromotion(String title, String body) throws BaseException;
	void updateOperationTime(List<OperationTime> operationTimes) throws BaseException;
	List<OperationTime> getOperationTimes() throws BaseException;
}
