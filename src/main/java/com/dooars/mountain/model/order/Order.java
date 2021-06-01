package com.dooars.mountain.model.order;

import com.google.common.base.MoreObjects;

import java.io.Serializable;

/**
 * @author Prantik Guha on 25-05-2021
 **/
public class Order implements Serializable {

    private static final long serialVersionUID = 2121212L;

    private long orderId;
    private Razorpay razorpay;
    private long mobileNumber;
    private long locationId;
    private long createdAt;
    private OrderDetails orderDetails;
    private PaymentMode paymentMode;
    private CurrentStatus currentStatus;
    private String completeAddress;
    private long lastUpdatedAt;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public Razorpay getRazorpay() {
        return razorpay;
    }

    public void setRazorpay(Razorpay razorpay) {
        this.razorpay = razorpay;
    }

    public long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public long getLocationId() {
        return locationId;
    }

    public void setLocationId(long locationId) {
        this.locationId = locationId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }

    public CurrentStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(CurrentStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getCompleteAddress() {
        return completeAddress;
    }

    public void setCompleteAddress(String completeAddress) {
        this.completeAddress = completeAddress;
    }

    public long getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(long lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("orderId", orderId)
                .add("razorpay", razorpay)
                .add("mobileNumber", mobileNumber)
                .add("locationId", locationId)
                .add("createdAt", createdAt)
                .add("orderDetails", orderDetails)
                .add("paymentMode", paymentMode)
                .add("currentStatus", currentStatus)
                .add("completeAddress", completeAddress)
                .add("lastUpdatedAt", lastUpdatedAt)
                .toString();
    }
}
