package com.dooars.mountain.web.controller;


import com.dooars.mountain.constants.AllEndPoints;
import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.service.sms.SMSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Prantik Guha on 31-05-2021
 **/
@CrossOrigin
@RestController
public class SMSController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SMSController.class);

    private final SMSService service;
    private final ControllerHelper helper;

    SMSController(SMSService service, ControllerHelper helper) {
        this.service = service;
        this.helper = helper;
    }

    @PostMapping(AllEndPoints.SEND_OTP_URL)
    public <T> ResponseEntity<T> sendOtp(@RequestParam("mobileNumber") long mobileNumber) {
        LOGGER.trace("Entering into sendOtp method in SMSController with{}", mobileNumber);
        try {
            return new ResponseEntity<T>((T) service.sendOTP(mobileNumber), HttpStatus.OK);
        } catch (BaseException exp) {
            return helper.constructErrorResponse(exp);
        }
    }

    @PostMapping(AllEndPoints.VERIFY_OTP_URL)
    public <T> ResponseEntity<T> verifyOtp(@RequestParam("mobileNumber") long mobileNumber, @RequestParam("otp") int otp) {
        LOGGER.trace("Entering into verifyOtp method in SMSController with {} {}", mobileNumber, otp);
        try {
            boolean flag = service.verifyOtp(otp, mobileNumber);
            if (flag)
                return new ResponseEntity<T>(HttpStatus.OK);
            else
                return new ResponseEntity<T>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BaseException exp) {
            return helper.constructErrorResponse(exp);
        }
    }


}
