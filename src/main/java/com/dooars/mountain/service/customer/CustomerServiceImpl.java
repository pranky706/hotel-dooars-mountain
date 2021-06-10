/**
 * 
 */
package com.dooars.mountain.service.customer;

import java.security.SignatureException;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import com.dooars.mountain.model.customer.CustomerToken;
import com.dooars.mountain.model.customer.Location;
import com.dooars.mountain.model.customer.Platform;
import com.dooars.mountain.model.deliveryboy.DeliveryBoy;
import com.dooars.mountain.model.item.Item;
import com.dooars.mountain.model.operation.OperationTime;
import com.dooars.mountain.model.order.*;
import com.dooars.mountain.repository.item.ItemRepository;
import com.dooars.mountain.repository.operation.OparetionTimeRepository;
import com.dooars.mountain.service.firebase.FireBaseService;
import com.dooars.mountain.service.print.PrintService;
import com.dooars.mountain.service.s3.AWSS3Service;
import com.dooars.mountain.web.commands.token.AddPushTokenCommand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.MulticastMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

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

	private final ItemRepository itemRepository;

	private final AWSS3Service awss3Service;

	private final PrintService printService;

	private final OparetionTimeRepository oparetionTimeRepository;

	private final FireBaseService fireBaseService;

	private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
	
	@Autowired
	CustomerServiceImpl(CustomerRepository customerRepo, ObjectMapper objectMapper, RestTemplate restTemplate,
						ItemRepository itemRepository, AWSS3Service awss3Service, PrintService printService,
						OparetionTimeRepository oparetionTimeRepository, FireBaseService fireBaseService) {
		this.customerRepo = customerRepo;
		this.objectMapper = objectMapper;
		this.restTemplate = restTemplate;
		this.itemRepository = itemRepository;
		this.awss3Service = awss3Service;
		this.printService = printService;
		this.oparetionTimeRepository = oparetionTimeRepository;
		this.fireBaseService = fireBaseService;
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
		Set<Integer> set = new HashSet<>(Arrays.asList(AllGolbalConstants.PINCODE_SET));
		if (! set.contains(location.getPincode()))
			return null;
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
		ZoneId zid = ZoneId.of("Asia/Kolkata");
		ZonedDateTime lt = ZonedDateTime.now(zid);
		long currentMiliSec = lt.toInstant().toEpochMilli();
		order.setCreatedAt(currentMiliSec);
		order.setLastUpdatedAt(currentMiliSec);
		if ( null != order.getLocation()) {
			Set<Integer> set = new HashSet<>(Arrays.asList(AllGolbalConstants.PINCODE_SET));
			if (! set.contains(order.getLocation().getPincode()))
				return null;
			Location location = customerRepo.getLocationByIdAndNumber(order.getMobileNumber(), order.getLocation().getLocationId());
			if ( null == location)
				return null;
		}
		customerRepo.addOrder(objectMapper.writeValueAsString(order), mobileNumber, orderId);
		sendAdminNotification();
		return order;
	}

	private void sendAdminNotification() throws BaseException {
		try {
			List<List<CustomerToken>> custTokens = customerRepo.getCustomerTokensAdmin();
			List<String> tokens = new ArrayList<>();
			System.out.println(custTokens);
			for (List<CustomerToken> customerTokens : custTokens) {
				for (CustomerToken customerToken : customerTokens) {
					if (Platform.WEB.toString().equals(customerToken.getPlatform().toString())) {
						tokens.addAll(customerToken.getPushTokens());
					}
				}
			}
			LOGGER.trace("Token size {}", tokens.size());
			BatchResponse batchResponse = fireBaseService.sendNotification(makeRequestForPushNotificationForAdmin(tokens));
			LOGGER.trace("Sending Promotion response {}", batchResponse.getResponses().size());
		} catch (BaseException e) {
			throw e;
		} catch (Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.SERVICE_LAYER, null);
		}
	}

	private MulticastMessage makeRequestForPushNotificationForAdmin(List<String> tokens) {
		String title = "Attention!! You have a new order request.";
		String body = "You have one pending order request, please take action.";
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("type", "NEW_ORDER");
		return fireBaseService.createMsg(tokens, title, body, dataMap);
	}

	@Override
	public boolean verifySign(Order order, long mobileNumber) throws BaseException, SignatureException {
		LOGGER.trace("Entering into verifySign method in CustomerServiceImpl with {} {}",order.toString(), mobileNumber);
		if (PaymentMode.ONLINE.equals(order.getPaymentMode()) && null != order.getRazorpay()) {
			String generatedSign = calculateRFC2104HMAC(order.getRazorpay().getOrderId() + "|" +
					order.getRazorpay().getPaymentId(), AllGolbalConstants.RAZORPAY_SECRET);
			if (generatedSign.equals(order.getRazorpay().getSignature()))
				return true;
			else
				return false;
		}
		if (PaymentMode.COD.equals(order.getPaymentMode()))
			return true;
		return false;
	}

	@Override
	public double getDeliveryCharge(double lat, double lon) {
		double distance = distance(lat, AllGolbalConstants.HOTEL_LAT, lon, AllGolbalConstants.HOTEL_LON);
		if ( distance <= 2)
			return 20;
		else if (distance > 2 && distance <= 4)
			return 30;
		else if (distance > 4 && distance <= 6)
			return 40;
		else if (distance > 6 && distance <= 8)
			return 50;
		else if (distance > 8 && distance <= 10)
			return 70;
		else
			return 100;
	}

	private double distance(double lat1,
							double lat2, double lon1,
							double lon2)
	{

		lon1 = Math.toRadians(lon1);
		lon2 = Math.toRadians(lon2);
		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

		// Haversine formula
		double dlon = lon2 - lon1;
		double dlat = lat2 - lat1;
		double a = Math.pow(Math.sin(dlat / 2), 2)
				+ Math.cos(lat1) * Math.cos(lat2)
				* Math.pow(Math.sin(dlon / 2),2);

		double c = 2 * Math.asin(Math.sqrt(a));
		double r = 6371;

		// calculate the result
		return(c * r);
	}

	private static String calculateRFC2104HMAC(String data, String secret)throws SignatureException
	{
		String result;
		try {

			// get an hmac_sha256 key from the raw secret bytes
			SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), HMAC_SHA256_ALGORITHM);

			// get an hmac_sha256 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
			mac.init(signingKey);

			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes());

			// base64-encode the hmac
			result = DatatypeConverter.printHexBinary(rawHmac).toLowerCase();

		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		return result;
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
			ZoneId zid = ZoneId.of("Asia/Kolkata");
			ZonedDateTime lt = ZonedDateTime.now(zid);
			long currentMiliSec = lt.toInstant().toEpochMilli();
			order.setLastUpdatedAt(currentMiliSec);
			customerRepo.updateOrder(objectMapper.writeValueAsString(order),mobileNumber,orderId);
			List<CustomerToken> customerTokens = customerRepo.getCustomerTokens(mobileNumber);
			List<String> tokens = new ArrayList<>();
			for (CustomerToken ct : customerTokens) {
				tokens.addAll(ct.getPushTokens());
			}
			LOGGER.trace("Token size {}", tokens.size());
			BatchResponse batchResponse = fireBaseService.sendNotification(makeRequestForPushNotification(tokens, currentStatus.toString(), order));
			LOGGER.trace("Sending Promotion response {}", batchResponse.getResponses().size());
			if (currentStatus.equals(CurrentStatus.OUT_FOR_DELIVERY)) {
				awss3Service.deleteFile(orderId + "_kot.pdf");
				awss3Service.deleteFile(orderId + "_bill.pdf");
			}
			if (currentStatus.equals(CurrentStatus.ACCEPTED))
				printService.createKOT(orderId);
			if (currentStatus.equals(CurrentStatus.READY_FOR_DELIVERY))
				printService.createBill(orderId);
		}
		return order;
	}

	private MulticastMessage makeRequestForPushNotification(List<String> tokens, String status, Order order) {
		String title = "Notification from Hotel Dooars Mountain";
		String body = "";
		if (status.equals(CurrentStatus.ACCEPTED.toString()))
			body = "Your order is accepted. Please wait for few minutes.";
		else if (status.equals(CurrentStatus.IN_KITCHEN.toString()))
			body = "Your food is in kitchen now. We are preparing it.";
		else if (status.equals(CurrentStatus.OUT_FOR_DELIVERY.toString()))
			body = "Your delicious food is on the way. Our delivery person " + order.getDeliveryBoy().getName() +
					" (+91" + order.getDeliveryBoy().getMobile() + ") will reach with your food shortly.";
		else if (status.equals(CurrentStatus.DELIVERED.toString()))
			body =  "We are very happy to serve you. We are waiting to meet you again very soon.";
		else if (status.equals(CurrentStatus.READY_FOR_DELIVERY.toString()))
			body =  "Your food is prepared. Our delivery person will leave with your food shortly.";
		else
			body = "Your current order status is " + status + ".";
		Map<String, String> dataMap = new HashMap<>();
		dataMap.put("type", "ORDER_STATUS_CHANGE");
		dataMap.put("data", String.valueOf(order.getOrderId()));
		return fireBaseService.createMsg(tokens, title, body, dataMap);
	}

	@Override
	public void sendPromotion(String title, String body) throws BaseException {
		LOGGER.trace("Entering into sendPromotion method in CustomerServiceImpl with {} {}", title, body);
		List<List<CustomerToken>> customerTokens = customerRepo.getCustomerTokensNotAdmin();
		List<String> tokens = new ArrayList<>();
		for (List<CustomerToken> customerTokenList : customerTokens) {
			for ( CustomerToken customerToken : customerTokenList) {
				tokens.addAll(customerToken.getPushTokens());
			}
		}
		LOGGER.trace("Token size {}", tokens.size());
		BatchResponse batchResponse = fireBaseService.sendPromotion(tokens, title, body);
		LOGGER.trace("Sending Promotion response {}", batchResponse.getResponses().size());

	}

	@Override
	public void updateOperationTime(List<OperationTime> operationTimes) throws BaseException {
		LOGGER.trace("Entering into updateOperationTime method in CustomerServiceImpl with {}", operationTimes);
		for (OperationTime operationTime : operationTimes) {
			oparetionTimeRepository.updateOperationTime(operationTime);
		}
	}

	@Override
	public List<OperationTime> getOperationTimes() throws BaseException {
		LOGGER.trace("Entering into getOperationTimes method in CustomerServiceImpl");
		return oparetionTimeRepository.getOperationTimes();
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
	public Order addDeliveryBoyToOrder(DeliveryBoy deliveryBoy, long orderId) throws BaseException, JsonProcessingException {
		LOGGER.trace("Entering into addDeliveryBoyToOrder method in CustomerServiceImpl with");
		Order order = customerRepo.getOrderById(orderId);
		if ( null == order)
			return null;
		order.setDeliveryBoy(deliveryBoy);
		customerRepo.updateOrder(objectMapper.writeValueAsString(order), order.getMobileNumber(), orderId);
		return order;
	}

	@Override
	public long getOrderCount(CurrentStatus currentStatus) throws BaseException {
		LOGGER.trace("Entering into getOrderCount method in CustomerServiceImpl with");
		return customerRepo.getOrderCount(currentStatus);
	}

	@Override
	public void removePushToken(long mobileNumber, Platform platform) throws BaseException, JsonProcessingException {
		LOGGER.trace("Entering into removePushToken method in CustomerServiceImpl with {} {} ", mobileNumber, platform.toString());
		List<CustomerToken> customerTokens = customerRepo.getCustomerTokens(mobileNumber);
		List<CustomerToken> newCustomerTokens = new ArrayList<>();
		if ( null != customerTokens && customerTokens.size() > 0) {
			for ( CustomerToken ct : customerTokens) {
				if ( !ct.getPlatform().equals(platform)) {
					newCustomerTokens.add(ct);
				}
			}
			customerRepo.updatePushToken(objectMapper.writeValueAsString(newCustomerTokens), mobileNumber);
		}
	}

	private List<Order>  getDailyOrders(LocalDate date) throws BaseException {
		ZoneId z = ZoneId.of( "Asia/Kolkata" );
		LocalDateTime sLDT = LocalDateTime.parse(date + "T00:00:00");
		LocalDateTime eLDT = LocalDateTime.parse(date + "T23:59:59");
		ZonedDateTime sZDT = ZonedDateTime.of(sLDT, z);
		ZonedDateTime eZDT = ZonedDateTime.of(eLDT, z);
		long start = sZDT.toInstant().toEpochMilli();
		long end = eZDT.toInstant().toEpochMilli();
		return customerRepo.getDailyOrders(start, end);
	}

	private long  getDailyTimeMili(LocalDate date) throws BaseException {
		ZoneId z = ZoneId.of( "Asia/Kolkata" );
		LocalDateTime sLDT = LocalDateTime.parse(date + "T00:00:00");
		ZonedDateTime sZDT = ZonedDateTime.of(sLDT, z);
		return sZDT.toInstant().toEpochMilli();
	}

	@Override
	public Map<String, Object> getDailySell(LocalDate date) throws BaseException {
		LOGGER.trace("Entering into getDailySell method in CustomerServiceImpl with {}", date);
		List<Order> orders = getDailyOrders(date);
		try {
			if (null != orders && orders.size() > 0) {
				List<DailySell> dailySells = new ArrayList<>();
				double total = 0.0;
				for ( Order order : orders) {
					DailySell dailySell = new DailySell();
					dailySell.setTotal(order.getOrderDetails().getTotal());
					dailySell.setOrderId(order.getOrderId());
					dailySell.setPaymentMode(order.getPaymentMode());
					dailySells.add(dailySell);
					total += dailySell.getTotal();
				}
				Map<String, Object> map = new HashMap<>();
				map.put("total", total);
				map.put("dailySells", dailySells);
				return map;
			}
		} catch (Exception e){
			throw new BaseException(e.getMessage(), AllGolbalConstants.SERVICE_LAYER, null);
		}
		return null;
	}

	@Override
	public Map<String, Object> getMonthlySell(int year, int month) throws BaseException {
		LOGGER.trace("Entering into getDailySell method in CustomerServiceImpl with {} {}", year, month);
		try {
			Map<String, Object> map = new HashMap<>();
			String sMonth = String.valueOf(month);
			double monthlyTotal = 0.0;
			if (month < 10)
				sMonth = "0" + sMonth;
			List<Map<String, Object>> monthlyReport = new ArrayList<>();
			YearMonth yearMonthObject = YearMonth.of(year, month);
			int daysInMonth = yearMonthObject.lengthOfMonth();
			for (int i = 1; i <= daysInMonth; i++) {
				String day = String.valueOf(i);
				if ( i < 10)
					day = "0" + i;
				LocalDate date = LocalDate.parse(String.valueOf(year) + "-" + sMonth + "-" + day);
				Map<String, Object> dailyReport = getDailySell(date);
				Map<String, Object> dailyData = new HashMap<>();
				dailyData.put("dailyReport", dailyReport);
				dailyData.put("day", getDailyTimeMili(date));
				if (null != dailyReport) {
					dailyData.put("dailyTotal", dailyReport.get("total"));
					monthlyTotal += (double)dailyReport.get("total");
				} else {
					dailyData.put("dailyTotal", 0.0);
				}
				monthlyReport.add(dailyData);
			}
			map.put("monthlyTotal", monthlyTotal);
			map.put("monthlyReport", monthlyReport);
			return map;
		} catch (BaseException e){
			throw e;
		} catch (Exception e){
			throw new BaseException(e.getMessage(), AllGolbalConstants.SERVICE_LAYER, null);
		}
	}

	@Override
	public List<Map<String, Object>> getItemWiseDailySell(LocalDate date) throws BaseException {
		LOGGER.trace("Entering into getItemWiseDailySell method in CustomerServiceImpl with {}", date);
		List<Item> items = itemRepository.getAllItem();
		Map<Integer, Object> itemMap = new HashMap<>();
		for ( Item item : items) {
			Map<String, Object> tempMap = new HashMap<>();
			tempMap.put("quantity", 0);
			tempMap.put("totalSellPrice", 0.0);
			tempMap.put("itemName", item.getItemName());
			tempMap.put("price", item.getPrice());
			tempMap.put("itemId", item.getItemId());
			itemMap.put(item.getItemId(), tempMap);
		}
		List<Order> orders = getDailyOrders(date);
		for (Order order : orders) {
			for (OrderItem item : order.getOrderDetails().getItems()) {
				Map<String, Object> tempMapNew = (Map<String, Object>) itemMap.get(item.getItemId());
				if (null != tempMapNew){
					int quantity = (int)tempMapNew.get("quantity") + item.getQuantity();
					tempMapNew.put("quantity", quantity);
					Double totalSellPrice = Double.valueOf(String.valueOf(tempMapNew.get("totalSellPrice"))) + (Double.valueOf(String.valueOf(tempMapNew.get("price"))) * item.getQuantity());
					tempMapNew.put("totalSellPrice", totalSellPrice);
				}
			}
		}
		List<Map<String, Object>> list = new ArrayList<>();
		for (Map.Entry<Integer, Object> entry : itemMap.entrySet()) {
			list.add((Map<String, Object>) entry.getValue());
		}
		Collections.sort(list, mapComparator);
		return list;
	}

	private List<Order>  getMonthlyOrders(LocalDate sDate, LocalDate eDate) throws BaseException {
		ZoneId z = ZoneId.of( "Asia/Kolkata" );
		LocalDateTime sLDT = LocalDateTime.parse(sDate + "T00:00:00");
		LocalDateTime eLDT = LocalDateTime.parse(eDate + "T23:59:59");
		ZonedDateTime sZDT = ZonedDateTime.of(sLDT, z);
		ZonedDateTime eZDT = ZonedDateTime.of(eLDT, z);
		long start = sZDT.toInstant().toEpochMilli();
		long end = eZDT.toInstant().toEpochMilli();
		return customerRepo.getDailyOrders(start, end);
	}

	@Override
	public List<Map<String, Object>> getItemWiseMonthlySell(int year, int month) throws BaseException {
		LOGGER.trace("Entering into getItemWiseMonthlySell method in CustomerServiceImpl with {} {}", year, month);
		try {
			List<Item> items = itemRepository.getAllItem();
			Map<Integer, Object> itemMap = new HashMap<>();
			for ( Item item : items) {
				Map<String, Object> tempMap = new HashMap<>();
				tempMap.put("quantity", 0);
				tempMap.put("totalSellPrice", 0.0);
				tempMap.put("itemName", item.getItemName());
				tempMap.put("price", item.getPrice());
				tempMap.put("itemId", item.getItemId());
				itemMap.put(item.getItemId(), tempMap);
			}
			String sMonth = String.valueOf(month);
			if (month < 10)
				sMonth = "0" + sMonth;
			List<Map<String, Object>> monthlyReport = new ArrayList<>();
			YearMonth yearMonthObject = YearMonth.of(year, month);
			int daysInMonth = yearMonthObject.lengthOfMonth();
			LocalDate sDate = LocalDate.parse(String.valueOf(year) + "-" + sMonth + "-" + "01");
			LocalDate eDate = LocalDate.parse(String.valueOf(year) + "-" + sMonth + "-" + daysInMonth);
			List<Order> orders = getMonthlyOrders(sDate, eDate);
			for (Order order : orders) {
				for (OrderItem item : order.getOrderDetails().getItems()) {
					Map<String, Object> tempMapNew = (Map<String, Object>) itemMap.get(item.getItemId());
					if (null != tempMapNew){
						int quantity = (int)tempMapNew.get("quantity") + item.getQuantity();
						tempMapNew.put("quantity", quantity);
						Double totalSellPrice = Double.valueOf(String.valueOf(tempMapNew.get("totalSellPrice"))) + (Double.valueOf(String.valueOf(tempMapNew.get("price"))) * item.getQuantity());
						tempMapNew.put("totalSellPrice", totalSellPrice);
					}
				}
			}
			List<Map<String, Object>> list = new ArrayList<>();
			for (Map.Entry<Integer, Object> entry : itemMap.entrySet()) {
				list.add((Map<String, Object>) entry.getValue());
			}
			Collections.sort(list, mapComparator);
			return list;
		}catch (BaseException e){
			throw e;
		} catch (Exception e){
			throw new BaseException(e.getMessage(), AllGolbalConstants.SERVICE_LAYER, null);
		}
	}

	public Comparator<Map<String, Object>> mapComparator = new Comparator<Map<String, Object>>() {
		public int compare(Map<String, Object> m1, Map<String, Object> m2) {
			return String.valueOf(m2.get("quantity")).compareTo(String.valueOf(m1.get("quantity")));
		}
	};

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
	@Override
	public Order getOrderByOrderId(long orderId) throws BaseException {
		LOGGER.trace("Entering into getOrderByOrderId method in CustomerServiceImpl with {}", orderId);
		return customerRepo.getOrderById(orderId);
	}

}
