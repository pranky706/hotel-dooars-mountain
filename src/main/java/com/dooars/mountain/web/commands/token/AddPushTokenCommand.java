package com.dooars.mountain.web.commands.token;

import com.dooars.mountain.model.customer.Platform;
import com.google.common.base.MoreObjects;

/**
 * @author Prantik Guha on 29-05-2021
 **/
public class AddPushTokenCommand {
    private long mobileNumber;
    private Platform platform;
    private String pushToken;

    public long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public String getPushToken() {
        return pushToken;
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("mobileNumber", mobileNumber)
                .add("platform", platform)
                .add("pushToken", pushToken)
                .toString();
    }
}
