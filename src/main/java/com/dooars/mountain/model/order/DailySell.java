package com.dooars.mountain.model.order;

import com.google.common.base.MoreObjects;

/**
 * @author Prantik Guha on 07-06-2021
 **/
public class DailySell {

    private long orderId;
    private double total;
    private PaymentMode paymentMode;

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("orderId", orderId)
                .add("total", total)
                .add("paymentMode", paymentMode)
                .toString();
    }
}
