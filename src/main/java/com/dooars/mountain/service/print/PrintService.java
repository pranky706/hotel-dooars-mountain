package com.dooars.mountain.service.print;

import com.dooars.mountain.model.common.BaseException;

/**
 * @author Prantik Guha on 08-06-2021
 **/
public interface PrintService {

    String createKOT(long orderId) throws BaseException;
    String createBill(long orderId) throws BaseException;
}
