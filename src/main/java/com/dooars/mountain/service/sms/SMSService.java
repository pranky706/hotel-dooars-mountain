package com.dooars.mountain.service.sms;

import com.dooars.mountain.model.common.BaseException;

import java.util.Map;

/**
 * @author Prantik Guha on 01-06-2021
 **/
public interface SMSService {

    Map<String, Object> sendOTP(long mobileNumber) throws BaseException;
    boolean verifyOtp(int otp, long mobileNumber) throws BaseException;
}
