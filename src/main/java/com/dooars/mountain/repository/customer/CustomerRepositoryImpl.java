/**
 * 
 */
package com.dooars.mountain.repository.customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dooars.mountain.model.customer.CustomerToken;
import com.dooars.mountain.model.customer.Location;
import com.dooars.mountain.model.order.CurrentStatus;
import com.dooars.mountain.model.order.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.dooars.mountain.constants.AllGolbalConstants;
import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.customer.Customer;

import io.vavr.control.Try;

/**
 * @author Prantik Guha
 * 19-May-2021 
 * CustomerRepositoryImpl.java
 */

@Repository
public class CustomerRepositoryImpl implements CustomerRepository{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerRepositoryImpl.class);
	
	private final NamedParameterJdbcTemplate jdbcTemplate;

	private final ObjectMapper objectMapper;
	
	private final String ADD_CUSTOMER = "INSERT INTO customer(mobileNumber, custName, isdelete, isAdmin)   \r\n" + 
			"VALUES (:mobileNumber, :custName, :isdelete, :isAdmin);";
	
	private final String GET_CUSTOMER = "select * from customer where mobileNumber = :mobileNumber and isdelete = :isdelete";

	private final String GET_LOCATION = "select location as location from location where mobileNumber = :mobileNumber and isdelete = :isdelete";
	
	private final String DELETE_CUSTOMER = "UPDATE customer set isdelete = :isdelete where mobileNumber = :mobileNumber";

	private final String DELETE_LOCATION = "UPDATE location set isdelete = :isdelete where locationId = :locationId and mobileNumber = :mobileNumber";

	private final String DELETE_ORDER = "UPDATE order set isdelete = :isdelete where orderId = :orderId and mobileNumber = :mobileNumber";

	private final String UPDATE_LOCATION = "UPDATE location set location = (:location)::json where locationId = :locationId and mobileNumber=:mobileNumber";

	private final String ADD_LOCATION = "INSERT INTO location(locationId, mobileNumber, location, isdelete) values(:locationId, :mobileNumber, (:location)::json, :isdelete)";

	private final String ADD_ORDER = "INSERT INTO orders(orderId, mobileNumber, orders, isdelete) values(:orderId, :mobileNumber, (:orders)::json, :isdelete)";


	private final String UPDATE_ORDER = "UPDATE orders set orders = (:orders)::json where orderId = :orderId and mobileNumber = :mobileNumber";

	private final String GET_ORDER = "select orders as orders from orders where mobileNumber = :mobileNumber";

	private final String GET_ORDER_BY_ID = "select orders as orders from orders where mobileNumber = :mobileNumber and orderId = :orderId";

	private final String GET_LOCATION_BY_ID = "select location as location from location where mobileNumber = :mobileNumber and locationId = :locationId";

	private final String GET_ALL_ORDER = "select orders as orders from orders LIMIT :noOfObjects OFFSET :offset";

	private final String GET_ALL_NOT_COMPLETED_ORDER = "select orders as orders from orders where orders.orders->>'currentStatus' != :currentStatus LIMIT :noOfObjects OFFSET :offset";

	private final String GET_ALL_ORDER_BY_STATUS = "select orders as orders from orders where orders.orders->>'currentStatus' =:currentStatus LIMIT :noOfObjects OFFSET :offset";

	private final String UPDATE_TOKEN = "UPDATE customer set customerTokens = (:customerTokens)::json where mobileNumber = :mobileNumber";

	private final String GET_TOKEN = "select customerTokens as customerTokens from customer where mobileNumber = :mobileNumber and isdelete = :isdelete";

	private final String GET_ORDER_COUNT = "select count(*) from orders";

	private final String GET_ORDER_COUNT_BY_STATUS = "select count(*) from orders where orders.orders->>'currentStatus' =:currentStatus";

	private final String GET_ORDER_COUNT_NOT_COMPLETED = "select count(*) from orders where orders.orders->>'currentStatus' !=:currentStatus";

	private final RowMapper<Customer> mapper = new RowMapper<Customer>() {
		
		@Override
		public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
			LOGGER.trace("Entering into customer mapper");
			Customer customer = new Customer();
			customer.setCustName(rs.getString("custName"));
			customer.setIsAdmin(rs.getString("isAdmin"));
			customer.setMobileNumber(rs.getLong("mobileNumber"));
			return customer;
		}
	};

	private final RowMapper<Location> locationMapper = new RowMapper<Location>() {

		@Override
		public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
			LOGGER.trace("Entering into location mapper");
			String jsonString = rs.getString("location");
			if ( null != jsonString && !"".equals(jsonString) && jsonString.startsWith("{")) {
				try {
					return objectMapper.readValue(jsonString, Location.class);
				} catch (JsonProcessingException e) {
					LOGGER.error(" Error while converting json list to java object in customer mapper");
					throw new SQLException("Error while converting json list to java object in customer mapper");
				}
			} else {
				return null;
			}
		}
	};

	private final RowMapper<Order> orderMapper = new RowMapper<Order>() {

		@Override
		public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
			LOGGER.trace("Entering into Order mapper");
			String jsonString = rs.getString("orders");
			if ( null != jsonString && !"".equals(jsonString) && jsonString.startsWith("{")) {
				try {
					return objectMapper.readValue(jsonString, Order.class);
				} catch (JsonProcessingException e) {
					LOGGER.error(" Error while converting json list to java object in order mapper");
					throw new SQLException("Error while converting json list to java object in order mapper");
				}
			} else {
				return null;
			}
		}
	};

	private final RowMapper<List<CustomerToken>> tokenMapper = new RowMapper<List<CustomerToken>>() {

		@Override
		public List<CustomerToken> mapRow(ResultSet rs, int rowNum) throws SQLException {
			LOGGER.trace("Entering into CustomerToken mapper");
			String jsonString = rs.getString("customerTokens");
			if ( null != jsonString && !"".equals(jsonString) && jsonString.startsWith("[")) {
				try {
					return objectMapper.readValue(jsonString, new TypeReference<List<CustomerToken>>(){});
				} catch (JsonProcessingException e) {
					LOGGER.error(" Error while converting json list to java object in CustomerToken mapper");
					throw new SQLException("Error while converting json list to java object in CustomerToken mapper");
				}
			} else {
				return new ArrayList<>();
			}
		}
	};
	
	@Autowired
	CustomerRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate,
						   ObjectMapper objectMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.objectMapper = objectMapper;
	}

	@Override
	public void addCustomer(Customer customer) throws BaseException {
		LOGGER.trace("Entering into addCustomer method in CustomerRepositoryImpl with {}", customer.toString());
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("mobileNumber", customer.getMobileNumber());
		namedParameters.addValue("custName", customer.getCustName());
		namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
		namedParameters.addValue("isAdmin", AllGolbalConstants.NO);
		try {
			jdbcTemplate.update(ADD_CUSTOMER, namedParameters);
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}
	}

	@Override
	public Customer getCustomer(long mobileNumber) throws BaseException {
		LOGGER.trace("Entering into getCustomer method in CustomerRepositoryImpl with {}", mobileNumber);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("mobileNumber", mobileNumber);
		namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
		return Try.ofSupplier(() -> DataAccessUtils.singleResult(jdbcTemplate.query(GET_CUSTOMER, namedParameters, mapper)))
			.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

	@Override
	public void deleteCustomer(long mobileNumber) throws BaseException {
		LOGGER.trace("Entering into deleteCustomer method in CustomerRepositoryImpl with {}", mobileNumber);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("mobileNumber", mobileNumber);
		namedParameters.addValue("isdelete", AllGolbalConstants.TRUE);
		try {
			jdbcTemplate.update(DELETE_CUSTOMER, namedParameters);
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}
		
	}

	@Override
	public long getOrderCount() throws BaseException {
		LOGGER.trace("Entering into getOrderCount method in CustomerRepositoryImpl with");
		return Try.ofSupplier(() -> DataAccessUtils.singleResult(jdbcTemplate.query(GET_ORDER_COUNT, (rs, row) -> rs.getInt("count"))))
				.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

	@Override
	public long getOrderCountNotCompleted() throws BaseException {
		LOGGER.trace("Entering into getOrderCountNotCompleted method in CustomerRepositoryImpl with");
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("currentStatus", CurrentStatus.DELIVERED.toString());
		return Try.ofSupplier(() -> DataAccessUtils.singleResult(jdbcTemplate.query(GET_ORDER_COUNT_NOT_COMPLETED, namedParameters, (rs, row) -> rs.getInt("count"))))
				.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

	@Override
	public long getOrderCount(CurrentStatus currentStatus) throws BaseException {
		LOGGER.trace("Entering into getOrderCount method in CustomerRepositoryImpl with");
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("currentStatus", currentStatus.toString());
		return Try.ofSupplier(() -> DataAccessUtils.singleResult(jdbcTemplate.query(GET_ORDER_COUNT_BY_STATUS, namedParameters,  (rs, row) -> rs.getInt("count"))))
				.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

	@Override
	public void updateLocation(String locationString, long mobileNumber, long locationId) throws BaseException {
		LOGGER.trace("Entering into updateLocation method in CustomerRepositoryImpl with {} {}", locationString, locationId);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("locationId", locationId);
		namedParameters.addValue("location", locationString);
		namedParameters.addValue("mobileNumber", mobileNumber);
		try {
			jdbcTemplate.update(UPDATE_LOCATION, namedParameters);
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}
	}

	@Override
	public void addLocation(String locationString, long mobileNumber, long locationId) throws BaseException {
		LOGGER.trace("Entering into addLocation method in CustomerRepositoryImpl with {} {} {}", locationString, locationId, mobileNumber);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("locationId", locationId);
		namedParameters.addValue("location", locationString);
		namedParameters.addValue("mobileNumber", mobileNumber);
		namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
		try {
			jdbcTemplate.update(ADD_LOCATION, namedParameters);
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}
	}

	@Override
	public void deleteLocation(long locationId, long mobileNumber) throws BaseException {
		LOGGER.trace("Entering into deleteLocation method in CustomerRepositoryImpl with {}", locationId);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("locationId", locationId);
		namedParameters.addValue("isdelete", AllGolbalConstants.TRUE);
		namedParameters.addValue("mobileNumber", mobileNumber);
		try {
			jdbcTemplate.update(DELETE_LOCATION, namedParameters);
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}
	}

	@Override
	public void updateOrder(String orderString, long mobileNumber, long orderId) throws BaseException {
		LOGGER.trace("Entering into updateOrder method in CustomerRepositoryImpl with {} {}", orderString, orderId);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("orderId", orderId);
		namedParameters.addValue("orders", orderString);
		namedParameters.addValue("mobileNumber", mobileNumber);
		try {
			jdbcTemplate.update(UPDATE_ORDER, namedParameters);
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}
	}

	@Override
	public void addOrder(String orderString, long mobileNumber, long orderId) throws BaseException {
		LOGGER.trace("Entering into addOrder method in CustomerRepositoryImpl with {} {} {}", orderString, orderId, mobileNumber);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("orderId", orderId);
		namedParameters.addValue("orders", orderString);
		namedParameters.addValue("mobileNumber", mobileNumber);
		namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
		try {
			jdbcTemplate.update(ADD_ORDER, namedParameters);
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}
	}

	@Override
	public void deleteOrder(long orderId, long mobileNumber) throws BaseException {
		LOGGER.trace("Entering into deleteOrder method in CustomerRepositoryImpl with {}", orderId);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("orderId", orderId);
		namedParameters.addValue("isdelete", AllGolbalConstants.TRUE);
		namedParameters.addValue("mobileNumber", mobileNumber);
		try {
			jdbcTemplate.update(DELETE_ORDER, namedParameters);
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}
	}

	@Override
	public void updatePushToken(String tokensString, long mobileNumber) throws BaseException {
		LOGGER.trace("Entering into updatePushToken method in CustomerRepositoryImpl with {} {}", tokensString, mobileNumber);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("mobileNumber", mobileNumber);
		namedParameters.addValue("customerTokens", tokensString);
		try {
			jdbcTemplate.update(UPDATE_TOKEN, namedParameters);
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}
	}

	@Override
	public List<CustomerToken> getCustomerTokens(long mobileNumber) throws BaseException {
		LOGGER.trace("Entering into getCustomerTokens method in CustomerRepositoryImpl with {}", mobileNumber);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("mobileNumber", mobileNumber);
		namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
		return Try.ofSupplier(() -> DataAccessUtils.singleResult(jdbcTemplate.query(GET_TOKEN, namedParameters, tokenMapper)))
				.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}


	@Override
	public List<Location> getLocations(long mobileNumber) throws BaseException {
		LOGGER.trace("Entering into getLocations method in CustomerRepositoryImpl with {}", mobileNumber);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("mobileNumber", mobileNumber);
		namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
		return Try.ofSupplier(() -> jdbcTemplate.query(GET_LOCATION, namedParameters, locationMapper))
				.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

	@Override
	public List<Order> getOrders(long mobileNumber) throws BaseException {
		LOGGER.trace("Entering into getOrders method in CustomerRepositoryImpl with {}", mobileNumber);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("mobileNumber", mobileNumber);
		return Try.ofSupplier(() -> jdbcTemplate.query(GET_ORDER, namedParameters, orderMapper))
				.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

	@Override
	public Order getOrderByIdAndNumber(long mobileNumber, long orderId) throws BaseException {
		LOGGER.trace("Entering into getOrderByIdAndNumber method in CustomerRepositoryImpl with {} {}", mobileNumber, orderId);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("mobileNumber", mobileNumber);
		namedParameters.addValue("orderId", orderId);
		return Try.ofSupplier(() -> DataAccessUtils.singleResult(jdbcTemplate.query(GET_ORDER_BY_ID, namedParameters, orderMapper)))
				.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

	@Override
	public Location getLocationByIdAndNumber(long mobileNumber, long locationId) throws BaseException {
		LOGGER.trace("Entering into getLocationByIdAndNumber method in CustomerRepositoryImpl with {} {}", mobileNumber, locationId);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("mobileNumber", mobileNumber);
		namedParameters.addValue("locationId", locationId);
		return Try.ofSupplier(() -> DataAccessUtils.singleResult(jdbcTemplate.query(GET_LOCATION_BY_ID, namedParameters, locationMapper)))
				.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

	@Override
	public List<Order> getAllOrders(int noOfObjects, int currentPage) throws BaseException {
		LOGGER.trace("Entering into getAllOrders method in CustomerRepositoryImpl with");
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("noOfObjects", noOfObjects);
		namedParameters.addValue("offset", (currentPage * noOfObjects) - noOfObjects);
		return Try.ofSupplier(() -> jdbcTemplate.query(GET_ALL_ORDER, namedParameters, orderMapper))
				.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

	@Override
	public List<Order> getAllOrdersNotCompleted(int noOfObjects, int currentPage) throws BaseException {
		LOGGER.trace("Entering into getAllOrdersNotCompleted method in CustomerRepositoryImpl with");
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("noOfObjects", noOfObjects);
		namedParameters.addValue("offset", (currentPage * noOfObjects) - noOfObjects);
		namedParameters.addValue("currentStatus", CurrentStatus.DELIVERED.toString());
		return Try.ofSupplier(() -> jdbcTemplate.query(GET_ALL_NOT_COMPLETED_ORDER, namedParameters, orderMapper))
				.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

	@Override
	public List<Order> getAllOrders(CurrentStatus currentStatus, int noOfObjects, int currentPage) throws BaseException {
		LOGGER.trace("Entering into getAllOrders method in CustomerRepositoryImpl with");
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("noOfObjects", noOfObjects);
		namedParameters.addValue("currentStatus", currentStatus.toString());
		namedParameters.addValue("offset", (currentPage * noOfObjects) - noOfObjects);
		return Try.ofSupplier(() -> jdbcTemplate.query(GET_ALL_ORDER_BY_STATUS, namedParameters, orderMapper))
				.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

}
