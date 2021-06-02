package com.dooars.mountain.web.controller;


import com.dooars.mountain.constants.AllEndPoints;
import com.dooars.mountain.constants.AllGolbalConstants;
import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.customer.Customer;
import com.dooars.mountain.service.sms.LoginService;
import com.dooars.mountain.web.commands.login.LoginCommand;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Prantik Guha on 31-05-2021
 **/
@CrossOrigin
@RestController
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    private final LoginService service;
    private final ControllerHelper helper;
    private final Validator loginCommandValidator;

    LoginController(LoginService service, ControllerHelper helper,
                    @Qualifier("loginCommandValidator") Validator loginCommandValidator) {
        this.service = service;
        this.helper = helper;
        this.loginCommandValidator = loginCommandValidator;
    }

    @PostMapping(AllEndPoints.LOGIN_URL)
    public <T> ResponseEntity<T> sendOtp(@RequestBody LoginCommand command, BindingResult bindingResult) {
        LOGGER.trace("Entering into sendOtp method in SMSController with{}", command.toString());
        try {
            loginCommandValidator.validate(command, bindingResult);
            if ( bindingResult.hasErrors())
                return helper.constructFieldErrorResponse(bindingResult);
            Customer customer = service.checkAdmin(command.getMobileNumber(), command.getPassword());
            if (null != customer){
                Map<String, Object> map = new HashMap<String, Object>();
                String token = getJWTToken(customer);
                map.put("token", token);
                map.put("success", true);
                return new ResponseEntity<T>((T) map, HttpStatus.OK);
            }
            else {
                Map<String, Object> map = new HashMap<>();
                map.put("message", "User is not an admin");
                map.put("success", false);
                return new ResponseEntity<T>((T) map,HttpStatus.FORBIDDEN);
            }
        } catch (BaseException exp) {
            return helper.constructErrorResponse(exp);
        }
    }

    private String getJWTToken(Customer customer) {
        String secretKey = "mySecretKey";
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList(AllGolbalConstants.ROLE_ADMIN);

        String token = Jwts
                .builder()
                .setId("JWT")
                .setSubject(String.valueOf(customer.getMobileNumber()))
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
