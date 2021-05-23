/**
 *
 */
package com.dooars.mountain.model.customer;

import java.io.Serializable;

import com.google.common.base.MoreObjects;

/**
 * @author Prantik Guha
 * 18-May-2021 
 * Customer.java
 */


public class Customer implements Serializable {


    private static final long serialVersionUID = 1L;

    private String custName;
    private long mobileNumber;
    private String isAdmin;

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("custName", custName)
                .add("mobileNumber", mobileNumber)
                .add("isAdmin", isAdmin)
                .toString();
    }


}
