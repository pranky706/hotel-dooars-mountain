package com.dooars.mountain.model.customer;

import com.google.common.base.MoreObjects;

import java.util.List;
import java.util.Set;

/**
 * @author Prantik Guha on 29-05-2021
 **/
public class CustomerToken {

    private Platform platform;
    private Set<String> pushTokens;

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public Set<String> getPushTokens() {
        return pushTokens;
    }

    public void setPushTokens(Set<String> pushTokens) {
        this.pushTokens = pushTokens;
    }

    public void addToken(String token) {
        this.pushTokens.add(token);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("platform", platform)
                .add("pushTokens", pushTokens)
                .toString();
    }
}
