/**
 * 
 */
package com.dooars.mountain.web.controller;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dooars.mountain.constants.AllGolbalConstants;
import com.dooars.mountain.model.common.ApiResult;
import com.dooars.mountain.model.common.Error;
import com.dooars.mountain.model.customer.Customer;
import com.dooars.mountain.service.customer.CustomerService;
import com.dooars.mountain.web.commands.AuthenticationCommand;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author Prantik Guha
 * 19-May-2021 
 * AuthenticationController.java
 */

@CrossOrigin
@RestController
public class AuthenticationController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);
	
	private final CustomerService service;
	private ControllerHelper helper;
	
	public AuthenticationController(CustomerService service, ControllerHelper helper) {
		this.service = service;
		this.helper = helper;
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping("authentication")
	public <T> ResponseEntity<T> validate(@RequestBody AuthenticationCommand command) {
		LOGGER.trace("Entering into validate method in AuthenticationController with{}", command.toString());
		try {
			if (null != command && String.valueOf(command.getMobileNumber()).length() == 10) {
				Customer customer = service.getCustomer(command.getMobileNumber());
				if ( null != customer) {
					Map<String, Object> map = new HashMap<String, Object>();
					String token = getJWTToken(customer.getMobileNumber());
					map.put("token", token);
					map.put("success", true);
					map.put("custName", customer.getCustName());
					return  (ResponseEntity<T>) helper.constructSuccessResponse(map);
				} else {
					return helper.constructNotFoundResponse();
				}
			
			} else {
				return new ResponseEntity<T>(HttpStatus.BAD_REQUEST);
			}
		} catch (Exception exp) {
			return helper.constructErrorResponse(exp);
		}
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

}
