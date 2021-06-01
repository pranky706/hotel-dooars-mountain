package com.dooars.mountain.web.commands.order;

import com.dooars.mountain.model.order.CurrentStatus;
import com.google.common.base.MoreObjects;

/**
 * @author Prantik Guha on 27-05-2021
 **/
public class UpdateOrderStatus {

    private long mobileNumber;
    private long orderId;
    private CurrentStatus currentStatus;

    public long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public CurrentStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(CurrentStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("mobileNumber", mobileNumber)
                .add("orderId", orderId)
                .add("currentStatus", currentStatus)
                .toString();
    }
}
