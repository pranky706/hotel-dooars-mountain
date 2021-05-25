/**
 * 
 */
package com.dooars.mountain.repository.customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dooars.mountain.model.customer.Location;
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

	private final String GET_LOCATION = "select locations as locations from customer where mobileNumber = :mobileNumber and isdelete = :isdelete";
	
	private final String DELETE_CUSTOMER = "UPDATE customer set isdelete = :isdelete where mobileNumber = :mobileNumber";

	private final String UPDATE_LOCATION = "UPDATE customer set locations = (:locations)::json where mobileNumber = :mobileNumber";
	
	private final RowMapper<Customer> mapper = new RowMapper<Customer>() {
		
		@Override
		public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
			LOGGER.trace("Entering into customer mapper");
			Customer customer = new Customer();
			customer.setCustName(rs.getString("custName"));
			customer.setIsAdmin(rs.getString("isAdmin"));
			customer.setMobileNumber(rs.getLong("mobileNumber"));
			String jsonString = rs.getString("locations");
			if ( null != jsonString && !"".equals(jsonString) && jsonString.startsWith("[")) {
				try {
					List<Location> locations = objectMapper.readValue(jsonString, new TypeReference<List<Location>>(){});
					customer.setLocations(locations);
				} catch (JsonProcessingException e) {
					LOGGER.error(" Error while converting json list to java object in customer mapper");
					throw new SQLException("Error while converting json list to java object in customer mapper");
				}
			} else {
				customer.setLocations(new ArrayList<>());
			}
			return customer;
		}
	};

	private final RowMapper<List<Location>> locationMapper = new RowMapper<List<Location>>() {

		@Override
		public List<Location> mapRow(ResultSet rs, int rowNum) throws SQLException {
			LOGGER.trace("Entering into location mapper");
			String jsonString = rs.getString("locations");
			if ( null != jsonString && !"".equals(jsonString) && jsonString.startsWith("[")) {
				try {
					return objectMapper.readValue(jsonString, new TypeReference<List<Location>>(){});
				} catch (JsonProcessingException e) {
					LOGGER.error(" Error while converting json list to java object in customer mapper");
					throw new SQLException("Error while converting json list to java object in customer mapper");
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
	public void updateLocation(String locationsString, long mobileNumber) throws BaseException {
		LOGGER.trace("Entering into updateLocation method in CustomerRepositoryImpl with {} {}", locationsString, mobileNumber);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("mobileNumber", mobileNumber);
		namedParameters.addValue("locations", locationsString);
		try {
			jdbcTemplate.update(UPDATE_LOCATION, namedParameters);
		} catch(Exception e) {
			throw new BaseException(e.getMessage(), AllGolbalConstants.REPO_LAYER, null);
		}
	}

	@Override
	public List<Location> getLocations(long mobileNumber) throws BaseException {
		LOGGER.trace("Entering into getLocations method in CustomerRepositoryImpl with {}", mobileNumber);
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("mobileNumber", mobileNumber);
		namedParameters.addValue("isdelete", AllGolbalConstants.FALSE);
		return Try.ofSupplier(() -> DataAccessUtils.singleResult(jdbcTemplate.query(GET_LOCATION, namedParameters, locationMapper)))
				.getOrElseThrow(throwable -> new BaseException(throwable.getMessage(), AllGolbalConstants.REPO_LAYER, null));
	}

}
