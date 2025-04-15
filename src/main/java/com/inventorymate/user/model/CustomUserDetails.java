package com.inventorymate.user.model;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails extends User {
    private final Long storeId;

    public CustomUserDetails(UserDetails user, Long storeId) {
        super(user.getUsername(), user.getPassword(), user.getAuthorities());
        this.storeId = storeId;
    }

    public Long getStoreId() {
        return storeId;
    }
}