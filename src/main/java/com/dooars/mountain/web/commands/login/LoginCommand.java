package com.dooars.mountain.web.commands.login;

import com.google.common.base.MoreObjects;

/**
 * @author Prantik Guha on 02-06-2021
 **/
public class LoginCommand {

    private long mobileNumber;
    private String password;

    public long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("mobileNumber", mobileNumber)
                .add("password", password)
                .toString();
    }
}
