package com.dooars.mountain.model.order;

import com.google.common.base.MoreObjects;

/**
 * @author Prantik Guha on 27-05-2021
 **/
public class OrderItem {

    private int quantity;
    private double total;
    private int itemId;
    private String itemName;
    private String itemImage;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("quantity", quantity)
                .add("total", total)
                .add("itemId", itemId)
                .add("itemName", itemName)
                .add("itemImage", itemImage)
                .toString();
    }
}
