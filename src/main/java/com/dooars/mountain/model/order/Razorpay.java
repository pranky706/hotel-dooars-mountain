package com.dooars.mountain.model.order;

import com.google.common.base.MoreObjects;

/**
 * @author Prantik Guha on 27-05-2021
 **/
public class Razorpay {
    private String paymentId;
    private String orderId;
    private String signature;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("paymentId", paymentId)
                .add("orderId", orderId)
                .add("signature", signature)
                .toString();
    }
}
