package com.dooars.mountain.model.deliveryboy;

import com.google.common.base.MoreObjects;

/**
 * @author Prantik Guha on 05-06-2021
 **/
public class DeliveryBoy {

    private String name;
    private long mobile;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMobile() {
        return mobile;
    }

    public void setMobile(long mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("mobile", mobile)
                .toString();
    }
}
