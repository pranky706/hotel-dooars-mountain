/**
 * 
 */
package com.dooars.mountain.service.customer;

import java.util.*;
import java.util.stream.Collectors;

import com.dooars.mountain.model.customer.Location;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	
	@Autowired
	CustomerServiceImpl(CustomerRepository customerRepo, ObjectMapper objectMapper) {
		this.customerRepo = customerRepo;
		this.objectMapper = objectMapper;
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
		Customer customer = customerRepo.getCustomer(mobileNumber);
		if ( null == customer)
			return null;
		int size = 0;
		if ( null != customer.getLocations() && customer.getLocations().size() > 0)
			size = customer.getLocations().size();
		else
			customer.setLocations(new ArrayList<>());
		location.setLocationId(Long.parseLong(mobileNumber + "" + size));
		List<Location> locations = customer.getLocations();
		locations.add(location);
		customerRepo.updateLocation(objectMapper.writeValueAsString(locations), mobileNumber);
		return location;
	}

	@Override
	public Location updateLocation(Location location, long mobileNumber) throws BaseException, JsonProcessingException {
		LOGGER.trace("Entering into updateLocation method in CustomerServiceImpl with {} {}",location.toString(), mobileNumber);
		Customer customer = customerRepo.getCustomer(mobileNumber);
		if ( null == customer)
			return null;
		if ( null != customer.getLocations() && customer.getLocations().size() == 0)
			customer.setLocations(new ArrayList<>());
		List<Location> locations = customer.getLocations();
		List<Location> newList = new ArrayList<>();
		boolean flag = false;
		for ( Location lo : locations) {
			if (lo.getLocationId() == location.getLocationId()) {
				newList.add(location);
				flag = true;
			} else {
				newList.add(lo);
			}
		}
		if (flag == false)
			return null;
		customerRepo.updateLocation(objectMapper.writeValueAsString(newList), mobileNumber);
		return location;
	}

	@Override
	public List<Location> getLocations(long mobileNumber) throws BaseException {
		LOGGER.trace("Entering into getLocations method in CustomerServiceImpl with {}", mobileNumber);
		return customerRepo.getLocations(mobileNumber);
	}

	@Override
	public Location deleteLocation(long locationId, long mobileNumber) throws BaseException, JsonProcessingException {
		LOGGER.trace("Entering into deleteLocation method in CustomerServiceImpl with {} {}", locationId, mobileNumber);
		Customer customer = customerRepo.getCustomer(mobileNumber);
		if ( null == customer)
			return null;
		if ( null != customer.getLocations() && customer.getLocations().size() == 0)
			customer.setLocations(new ArrayList<>());
		List<Location> locations = customer.getLocations();
		List<Location> newList = new ArrayList<>();
		Location location = null;
		boolean flag = false;
		for ( Location lo : locations) {
			if (lo.getLocationId() != locationId) {
				newList.add(lo);
			} else {
				location = lo;
				flag = true;
			}
		}
		if (flag == false)
			return null;
		customerRepo.updateLocation(objectMapper.writeValueAsString(newList), mobileNumber);
		return location;
	}

}
