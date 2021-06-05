package com.dooars.mountain.model.order;

import com.dooars.mountain.model.customer.Location;
import com.dooars.mountain.model.deliveryboy.DeliveryBoy;
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
    private String custName;
    private Location location;
    private long createdAt;
    private OrderDetails orderDetails;
    private PaymentMode paymentMode;
    private CurrentStatus currentStatus;
    private String completeAddress;
    private long lastUpdatedAt;
    private DeliveryBoy deliveryBoy;


    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public DeliveryBoy getDeliveryBoy() {
        return deliveryBoy;
    }

    public void setDeliveryBoy(DeliveryBoy deliveryBoy) {
        this.deliveryBoy = deliveryBoy;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("orderId", orderId)
                .add("razorpay", razorpay)
                .add("mobileNumber", mobileNumber)
                .add("custName", custName)
                .add("location", location)
                .add("createdAt", createdAt)
                .add("orderDetails", orderDetails)
                .add("paymentMode", paymentMode)
                .add("currentStatus", currentStatus)
                .add("completeAddress", completeAddress)
                .add("lastUpdatedAt", lastUpdatedAt)
                .add("deliveryBoy", deliveryBoy)
                .toString();
    }
}
