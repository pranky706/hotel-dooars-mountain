package com.dooars.mountain.service.sms;

import com.dooars.mountain.constants.AllGolbalConstants;
import com.dooars.mountain.constants.SmsConstants;
import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.repository.customer.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Prantik Guha on 01-06-2021
 **/
@Service
public class SMSServiceImpl implements SMSService{

    private static final Logger LOGGER = LoggerFactory.getLogger(SMSServiceImpl.class);

    private final CustomerRepository repository;

    @Autowired
    SMSServiceImpl(CustomerRepository repository) {
        this.repository = repository;
    }


    @Override
    public Map<String, Object> sendOTP(long mobileNumber) throws BaseException {
        LOGGER.trace("Entering into sendOTP method in SMSServiceImpl with {}", mobileNumber);
        Map<String, Object> map = new HashMap<>();
        int otp = generateOTP();
        map.put("otp", otp);
        repository.updateOTP(otp, mobileNumber);
        return map;
    }

    @Override
    public boolean verifyOtp(int otp, long mobileNumber) throws BaseException {
        LOGGER.trace("Entering into verifyOtp method in SMSServiceImpl with {} {}", mobileNumber, otp);
        int savedOtp = repository.getOtp(mobileNumber);
        if (0 == savedOtp)
            return false;
        if ( savedOtp == otp)
            return true;
        else
            return false;
    }


    private int generateOTP() {
        int randomPin   =(int) (Math.random()*9000)+1000;
        return randomPin;
    }
}
