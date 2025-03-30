package com.inventorymate.authentication.service.impl;

import com.inventorymate.authentication.model.AuthResponse;
import com.inventorymate.authentication.model.LoginRequest;
import com.inventorymate.authentication.model.RegisterStoreRequest;
import com.inventorymate.authentication.service.AuthService;
import com.inventorymate.exception.ValidationException;
import com.inventorymate.jwt.JwtService;
import com.inventorymate.user.model.Store;
import com.inventorymate.user.repository.StoreRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final StoreRepository storeRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(StoreRepository storeRepository, JwtService jwtService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.storeRepository = storeRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse registerStore(RegisterStoreRequest registerStoreRequest) {
        Store restaurant = Store.builder()
                .username(registerStoreRequest.getUsername())
                .password(passwordEncoder.encode(registerStoreRequest.getPassword()))
                .storeName(registerStoreRequest.getStoreName())
                .build();
        storeRepository.save(restaurant);
        return AuthResponse.builder()
                .token(jwtService.getToken(restaurant))
                .id(restaurant.getId())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        var store = storeRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ValidationException("User not found"));

        String token = jwtService.getToken(store);

        return AuthResponse.builder()
                .token(token)
                .id(store.getId())
                .storeName(store.getStoreName())
                .build();
    }

    @Override
    public void existsUserByUsername(RegisterStoreRequest registerStoreRequest) {
        boolean existsInStore = storeRepository.existsByUsername(registerStoreRequest.getUsername());

        if (existsInStore) {
            throw new ValidationException("Username already exists " + registerStoreRequest.getUsername());
        }
    }

}
