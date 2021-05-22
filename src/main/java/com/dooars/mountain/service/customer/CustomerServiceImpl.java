/**
 * 
 */
package com.dooars.mountain.service.customer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	
	@Autowired
	CustomerServiceImpl(CustomerRepository customerRepo) {
		this.customerRepo = customerRepo;
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

}
