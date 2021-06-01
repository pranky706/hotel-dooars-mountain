package com.dooars.mountain.model.customer;

import com.google.common.base.MoreObjects;

import java.util.List;

/**
 * @author Prantik Guha on 29-05-2021
 **/
public class CustomerTokens {

    private List<CustomerToken> customerTokens;

    public List<CustomerToken> getCustomerTokens() {
        return customerTokens;
    }

    public void setCustomerTokens(List<CustomerToken> customerTokens) {
        this.customerTokens = customerTokens;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("customerTokens", customerTokens)
                .toString();
    }
}
