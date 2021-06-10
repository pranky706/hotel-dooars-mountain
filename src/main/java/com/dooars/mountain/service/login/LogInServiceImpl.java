package com.dooars.mountain.service.login;

import com.dooars.mountain.constants.LoginConstants;
import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.customer.Customer;
import com.dooars.mountain.repository.customer.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author Prantik Guha on 01-06-2021
 **/
@Service
public class LogInServiceImpl implements LoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogInServiceImpl.class);

    private final CustomerRepository repository;

    @Autowired
    LogInServiceImpl(CustomerRepository repository) {
        this.repository = repository;
    }


    @Override
    public Customer checkAdmin(long mobileNumber, String password) throws BaseException {
        LOGGER.trace("Entering into checkAdmin method in SMSServiceImpl with {} {}", mobileNumber, password);
        Customer customer = repository.getCustomer(mobileNumber);
        if ( null == customer)
            return null;
        if ( null != customer && "N".equals(customer.getIsAdmin()))
            return null;
        ZoneId zid = ZoneId.of("Asia/Kolkata");
        ZonedDateTime lt = ZonedDateTime.now(zid);
        int dd = lt.getDayOfMonth();
        String ddVal = String.valueOf(dd);
        if ( dd < 10)
            ddVal = "0" + ddVal;
        int mm = lt.getMonthValue();
        String mmVal = String.valueOf(mm);
        if ( mm < 10)
            mmVal = "0" + mmVal;
        int yyyy = lt.getYear();
        String pass = LoginConstants.PASS + ddVal + "_" + mmVal + "_" + yyyy;
        System.out.println(pass);
        if (pass.equals(password))
            return customer;
        else
            return null;
    }
}
