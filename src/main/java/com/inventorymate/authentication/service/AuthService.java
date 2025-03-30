package com.inventorymate.authentication.service;

import com.inventorymate.authentication.model.AuthResponse;
import com.inventorymate.authentication.model.LoginRequest;
import com.inventorymate.authentication.model.RegisterStoreRequest;

public interface AuthService {
    public abstract AuthResponse registerStore(RegisterStoreRequest registerRestaurantRequest);
    public abstract AuthResponse login(LoginRequest loginRequest);
    public void existsUserByUsername(RegisterStoreRequest registerRequest);
}
