/**
 * 
 */
package com.dooars.mountain.service.customer;

import java.util.*;
import java.util.stream.Collectors;

import com.dooars.mountain.constants.FireBaseConstants;
import com.dooars.mountain.model.customer.CustomerToken;
import com.dooars.mountain.model.customer.Location;
import com.dooars.mountain.model.order.CurrentStatus;
import com.dooars.mountain.model.order.Order;
import com.dooars.mountain.web.commands.token.AddPushTokenCommand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import com.dooars.mountain.constants.AllGolbalConstants;
import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.customer.Customer;
import com.dooars.mountain.repository.customer.CustomerRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.web.client.RestTemplate;

/**
 * @author Prantik Guha
 * 19-May-2021 
 * CustomerServiceImpl.java
 */

@Service
public class CustomerServiceImpl implements CustomerService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerServiceImpl.class);
	
	private final CustomerRepository customerRepo;

	private final ObjectMapper objectMapper;

	private final RestTemplate restTemplate;
	
	@Autowired
	CustomerServiceImpl(CustomerRepository customerRepo, ObjectMapper objectMapper, RestTemplate restTemplate) {
		this.customerRepo = customerRepo;
		this.objectMapper = objectMapper;
		this.restTemplate = restTemplate;
	}

	@Override
	public Map<String, Object> addCustomer(Customer customer) throws BaseException {
		LOGGER.trace("Entering into addCustomer method in CustomerServiceImpl with{}", customer.toString());
		try {
			customerRepo.addCustomer(customer);
		} catch (BaseException exp) {
			if (exp.getMessage().contains(AllGolbalConstants.DUPLICATE_MSG)) {
				return null;
			} else {
				throw exp;
			}
		}
		Map<String, Object> map = new HashMap<String, Object>();
		String token = getJWTToken(customer.getMobileNumber());
		map.put("token", token);
		map.put("success", true);
		return map; 
	}
	
	private String getJWTToken(long mobileNumber) {
		String secretKey = "mySecretKey";
		List<GrantedAuthority> grantedAuthorities = AuthorityUtils
				.commaSeparatedStringToAuthorityList("ROLE_USER");
		
		String token = Jwts
				.builder()
				.setId("JWT")
				.setSubject(String.valueOf(mobileNumber))
				.claim("authorities",
						grantedAuthorities.stream()
								.map(GrantedAuthority::getAuthority)
								.collect(Collectors.toList()))
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 900000000))
				.signWith(SignatureAlgorithm.HS512,
						secretKey.getBytes()).compact();

		return "Bearer " + token;
	}

	@Override
	public Customer getCustomer(long mobileNumber) throws BaseException {
		LOGGER.trace("Entering into getCustomer method in CustomerServiceImpl with{}", mobileNumber);
		return customerRepo.getCustomer(mobileNumber);
	}

	@Override
	public Location addLocation(Location location, long mobileNumber) throws BaseException, JsonProcessingException {
		LOGGER.trace("Entering into addLocation method in CustomerServiceImpl with {} {}",location.toString(), mobileNumber);
		long locationId = generateUniqueId();
		location.setLocationId(locationId);
		customerRepo.addLocation(objectMapper.writeValueAsString(location), mobileNumber, locationId);
		return location;
	}

	@Override
	public Order addOrder(Order order, long mobileNumber) throws BaseException, JsonProcessingException {
		LOGGER.trace("Entering into addOrder method in CustomerServiceImpl with {} {}",order.toString(), mobileNumber);
		long orderId = generateUniqueId();
		order.setOrderId(orderId);
		order.setCurrentStatus(CurrentStatus.PLACED);
		if ( null != order.getLocation()) {
			Location location = customerRepo.getLocationByIdAndNumber(order.getMobileNumber(), order.getLocation().getLocationId());
			if ( null == location)
				return null;
		}
		customerRepo.addOrder(objectMapper.writeValueAsString(order), mobileNumber, orderId);
		return order;
	}

	private Long generateUniqueId() {
		UUID idOne = UUID.randomUUID();
		String str = "" + idOne;
		int uid = str.hashCode();
		String filterStr = "" + uid;
		str = filterStr.replaceAll("-", "");
		return Long.parseLong(str);
	}

	@Override
	public Location updateLocation(Location location, long mobileNumber) throws BaseException, JsonProcessingException {
		LOGGER.trace("Entering into updateLocation method in CustomerServiceImpl with {} {}",location.toString(), mobileNumber);
		customerRepo.updateLocation(objectMapper.writeValueAsString(location), mobileNumber,location.getLocationId());
		return location;
	}

	@Override
	public Order updateOrderStatus(long orderId, long mobileNumber, CurrentStatus currentStatus) throws BaseException, JsonProcessingException {
		LOGGER.trace("Entering into updateLocation method in CustomerServiceImpl with {} {} {}",orderId, mobileNumber, currentStatus);
		Order order = customerRepo.getOrderByIdAndNumber(mobileNumber, orderId);
		if (null != order) {
			order.setCurrentStatus(currentStatus);
			customerRepo.updateOrder(objectMapper.writeValueAsString(order),mobileNumber,orderId);
			List<CustomerToken> customerTokens = customerRepo.getCustomerTokens(mobileNumber);
			for (CustomerToken ct : customerTokens) {
				for (String token : ct.getPushTokens()) {
					Map<String, Object> request = makeRequestForPushNotification(token, currentStatus.toString());
					HttpHeaders headers = new HttpHeaders();
					headers.add("Authorization", FireBaseConstants.KEY);
					HttpEntity<Map> entity = new HttpEntity<Map>(request,headers);
					String response = restTemplate.exchange(
							FireBaseConstants.URL, HttpMethod.POST, entity, String.class).getBody();
					LOGGER.trace(" Response from firebase {}", response);
				}
			}
		}
		return order;
	}

	private Map<String, Object> makeRequestForPushNotification(String token, String status) {
		Map<String, Object> map = new HashMap<>();
		map.put("to", token);
		map.put("collapse_key", "type_a");
		Map<String, Object> notificationMap = new HashMap<>();
		notificationMap.put("title", "Notification from Hotel Dooars Mountain");
		notificationMap.put("body", "Your order status is " + status + ".");
		map.put("notification", notificationMap);
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("message", "Hello");
		map.put("data", dataMap);
		return map;
	}

	@Override
	public void addPushToken(AddPushTokenCommand command) throws BaseException, JsonProcessingException {
		LOGGER.trace("Entering into addPushToken method in CustomerServiceImpl with {}", command.toString());
		Customer customer = customerRepo.getCustomer(command.getMobileNumber());
		if ( null != customer) {
			List<CustomerToken> tokens = customerRepo.getCustomerTokens(command.getMobileNumber());
			if ( null != tokens ) {
				CustomerToken customerToken = null;
				for ( CustomerToken  ct : tokens) {
					if ( ct.getPlatform().equals(command.getPlatform())) {
						customerToken = ct;
					}
				}
				if ( null == customerToken){
					customerToken = new CustomerToken();
					customerToken.setPlatform(command.getPlatform());
					customerToken.setPushTokens(new HashSet<>());
					tokens.add(customerToken);
				}
				customerToken.addToken(command.getPushToken());
			} else {
				tokens = new ArrayList<>();
				CustomerToken customerToken = new CustomerToken();
				customerToken.setPlatform(command.getPlatform());
				customerToken.setPushTokens(new HashSet<>());
				customerToken.addToken(command.getPushToken());
				tokens.add(customerToken);
			}
			customerRepo.updatePushToken(objectMapper.writeValueAsString(tokens), command.getMobileNumber());
		}
	}

	@Override
	public List<CustomerToken> getTokens(long mobileNumber) throws BaseException {
		LOGGER.trace("Entering into getTokens method in CustomerServiceImpl with {}", mobileNumber);
		return customerRepo.getCustomerTokens(mobileNumber);
	}

	@Override
	public long getOrderCount() throws BaseException {
		LOGGER.trace("Entering into getOrderCount method in CustomerServiceImpl with");
		return customerRepo.getOrderCount();
	}

	@Override
	public long getOrderCountNotCompleted() throws BaseException {
		LOGGER.trace("Entering into getOrderCountNotCompleted method in CustomerServiceImpl with");
		return customerRepo.getOrderCountNotCompleted();
	}

	@Override
	public long getOrderCount(CurrentStatus currentStatus) throws BaseException {
		LOGGER.trace("Entering into getOrderCount method in CustomerServiceImpl with");
		return customerRepo.getOrderCount(currentStatus);
	}

	@Override
	public List<Location> getLocations(long mobileNumber) throws BaseException {
		LOGGER.trace("Entering into getLocations method in CustomerServiceImpl with {}", mobileNumber);
		return customerRepo.getLocations(mobileNumber);
	}

	@Override
	public List<Order> getOrders(long mobileNumber) throws BaseException {
		LOGGER.trace("Entering into getOrders method in CustomerServiceImpl with {}", mobileNumber);
		return customerRepo.getOrders(mobileNumber);
	}

	@Override
	public List<Order> getAllOrders(int noOfObjects, int currentPage) throws BaseException {
		LOGGER.trace("Entering into getAllOrders method in CustomerServiceImpl with");
		return customerRepo.getAllOrders(noOfObjects, currentPage);
	}

	@Override
	public List<Order> getAllOrdersNotCompleted(int noOfObjects, int currentPage) throws BaseException {
		LOGGER.trace("Entering into getAllOrdersNotCompleted method in CustomerServiceImpl with");
		return customerRepo.getAllOrdersNotCompleted(noOfObjects, currentPage);
	}

	@Override
	public List<Order> getAllOrdersByStatus(CurrentStatus currentStatus, int noOfObjects, int currentPage) throws BaseException {
		LOGGER.trace("Entering into getAllOrdersByStatus method in CustomerServiceImpl with {}", currentStatus);
		return customerRepo.getAllOrders(currentStatus, noOfObjects, currentPage);
	}

	@Override
	public Location deleteLocation(long locationId, long mobileNumber) throws BaseException, JsonProcessingException {
		LOGGER.trace("Entering into deleteLocation method in CustomerServiceImpl with {} {}", locationId, mobileNumber);
		Location location = customerRepo.getLocationByIdAndNumber(mobileNumber, locationId);
		customerRepo.deleteLocation(locationId, mobileNumber);
		return location;
	}

	@Override
	public Order deleteOrder(long orderId, long mobileNumber) throws BaseException, JsonProcessingException {
		LOGGER.trace("Entering into deleteOrder method in CustomerServiceImpl with {} {}", orderId, mobileNumber);
		Order order = customerRepo.getOrderByIdAndNumber(mobileNumber, orderId);
		customerRepo.deleteOrder(orderId, mobileNumber);
		return order;
	}

}
