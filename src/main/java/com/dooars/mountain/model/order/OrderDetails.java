package com.dooars.mountain.model.order;

import com.google.common.base.MoreObjects;

import java.util.List;

/**
 * @author Prantik Guha on 27-05-2021
 **/
public class OrderDetails {

    private double subtotal;
    private double subtotalAfterOffer;
    private double total;
    private double tax;
    private double offer;
    private double deliveryCharges;
    private List<OrderItem> items;


    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getSubtotalAfterOffer() {
        return subtotalAfterOffer;
    }

    public void setSubtotalAfterOffer(double subtotalAfterOffer) {
        this.subtotalAfterOffer = subtotalAfterOffer;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getOffer() {
        return offer;
    }

    public void setOffer(double offer) {
        this.offer = offer;
    }

    public double getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(double deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("subtotal", subtotal)
                .add("subtotalAfterOffer", subtotalAfterOffer)
                .add("total", total)
                .add("tax", tax)
                .add("offer", offer)
                .add("deliveryCharges", deliveryCharges)
                .add("items", items)
                .toString();
    }
}
