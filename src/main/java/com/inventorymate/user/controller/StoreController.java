package com.inventorymate.user.controller;

import com.inventorymate.exception.ResourceNotFoundException;
import com.inventorymate.exception.ValidationException;
import com.inventorymate.user.model.Store;
import com.inventorymate.user.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/InventoryMate/v1/stores")
public class StoreController {

    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/stores
    // Method: GET
    // Description: Get all stores
    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<List<Store>> getAllStores() {
        List<Store> stores = storeService.getAllStores();
        return stores.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(stores);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/stores/{storeId}
    // Method: GET
    // Description: Get store by id
    @Transactional(readOnly = true)
    @GetMapping("/{storeId}")
    public ResponseEntity<Store> getStoreById(@PathVariable(name = "storeId") Long storeId) {
        Store store = storeService.getStoreById(storeId);
        return ResponseEntity.ok(store);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/stores
    // Method: POST
    // Description: Save store
    @Transactional
    @PostMapping
    public ResponseEntity<Store> registerStore(@RequestBody Store storeRequest) {
        Store savedStore = storeService.createStore(storeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStore);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/stores/{storeId}
    // Method: PUT
    // Description: Update store
    @Transactional
    @PutMapping("/{storeId}")
    public ResponseEntity<Store> updateStore(@PathVariable(name = "storeId") Long storeId, @RequestBody Store storeRequest) {
        Store store = storeService.updateStore(storeRequest, storeId);
        return ResponseEntity.ok(store);
    }

    // URL: http://localhost:8081/api/InventoryMate/v1/store/{storeId}
    // Method: DELETE
    // Description: Delete store
    @Transactional
    @DeleteMapping("/{storeId}")
    public ResponseEntity<String> deleteStore(@PathVariable(name = "storeId") Long storeId) {
        storeService.deleteStore(storeId);
        return ResponseEntity.noContent().build();
    }

    // Global Exception Handling for Not Found & Validation Exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
