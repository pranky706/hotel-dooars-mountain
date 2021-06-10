package com.dooars.mountain.service.login;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.customer.Customer;


/**
 * @author Prantik Guha on 01-06-2021
 **/
public interface LoginService {

    Customer checkAdmin(long mobileNumber, String password) throws BaseException;
}
