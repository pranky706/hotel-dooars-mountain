package com.dooars.mountain.repository.deliveryboy;

import com.dooars.mountain.model.common.BaseException;
import com.dooars.mountain.model.deliveryboy.DeliveryBoy;

import java.util.List;

/**
 * @author Prantik Guha on 05-06-2021
 **/
public interface DeliveryBoyRepository {

    void addDeliveryBoy(DeliveryBoy deliveryBoy) throws BaseException;
    DeliveryBoy getBoyByNumber( long mobile) throws BaseException;
    DeliveryBoy checkBoyByNumber( long mobile) throws BaseException;
    List<DeliveryBoy> getAllDeliveryBoy() throws BaseException;
    void updateDeliveryBoy(DeliveryBoy deliveryBoy) throws BaseException;
    void deleteDeliveryBoy(long mobile) throws BaseException;
    void undoDeleteDeliveryBoy(long mobile) throws BaseException;
}
