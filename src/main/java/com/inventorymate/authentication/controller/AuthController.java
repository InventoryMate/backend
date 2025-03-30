package com.inventorymate.authentication.controller;

import com.inventorymate.authentication.model.AuthResponse;
import com.inventorymate.authentication.model.LoginRequest;
import com.inventorymate.authentication.model.RegisterStoreRequest;
import com.inventorymate.authentication.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("api/InventoryMate/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/auth/register-store
    // Method: POST
    @Transactional
    @PostMapping("/register-store")
    public ResponseEntity<AuthResponse> registerRestaurant(@RequestBody RegisterStoreRequest request) {
        // Verificar si ya existe el nombre de usuario
        authService.existsUserByUsername(request);

        // Registrar el restaurante
        AuthResponse registeredRestaurant = authService.registerStore(request);
        return new ResponseEntity<>(registeredRestaurant, HttpStatus.CREATED);
    }


    // URL: http://localhost:8081/api/InventoryMate/v1/auth/login
    // Method: POST
    @Transactional(readOnly = true)
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest request) {
        AuthResponse loggedUser = authService.login(request);
        return new ResponseEntity<>(loggedUser, HttpStatus.OK);
    }
}
