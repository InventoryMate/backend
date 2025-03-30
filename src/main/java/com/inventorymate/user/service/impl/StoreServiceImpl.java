package com.inventorymate.user.service.impl;

import com.inventorymate.exception.ResourceNotFoundException;
import com.inventorymate.exception.ValidationException;
import com.inventorymate.user.model.Store;
import com.inventorymate.user.repository.StoreRepository;
import com.inventorymate.user.service.StoreService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreServiceImpl  implements StoreService {

    private final StoreRepository storeRepository;

    public StoreServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    @Override
    public Store getStoreById(Long storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock with Id " + storeId + " not found"));
    }

    @Override
    public Store createStore(Store store) {
        return storeRepository.save(store);
    }

    @Override
    public Store updateStore(Store store, Long storeId) {
        Store storeToUpdate = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Store with Id " + storeId + " not found"));

        storeToUpdate.setStoreName(store.getStoreName());

        return storeRepository.save(storeToUpdate);
    }

    @Override
    public void deleteStore(Long storeId) {
        if (!storeRepository.existsById(storeId)) {
            throw new ResourceNotFoundException("Store with Id " + storeId + " not found");
        }
        storeRepository.deleteById(storeId);
    }

    @Override
    public void existsStoreById(Long storeId) {
        if (!storeRepository.existsById(storeId)) {
            throw new ValidationException("Store with Id " + storeId + " not found");
        }
    }
}
