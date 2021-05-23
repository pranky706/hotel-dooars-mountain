/**
 * 
 */
package com.dooars.mountain.repository.customer;

import java.sql.ResultSet;
import java.sql.SQLException;

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
	
	private final String ADD_CUSTOMER = "INSERT INTO customer(mobileNumber, custName, isdelete, isAdmin)   \r\n" + 
			"VALUES (:mobileNumber, :custName, :isdelete, :isAdmin);";
	
	private final String GET_CUSTOMER = "select * from customer where mobileNumber = :mobileNumber and isdelete = :isdelete";
	
	private final String DELETE_CUSTOMER = "UPDATE user set isdelete = :isdelete where mobileNumber = :mobileNumber";
	
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
	
	@Autowired
	CustomerRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
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

}
