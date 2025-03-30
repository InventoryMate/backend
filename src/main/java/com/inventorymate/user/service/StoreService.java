package com.inventorymate.user.service;

import com.inventorymate.user.model.Store;

import java.util.List;

public interface StoreService {
    public abstract List<Store> getAllStores();
    public abstract Store getStoreById(Long storeId);
    public abstract Store createStore(Store store);
    public abstract Store updateStore(Store store,  Long storeId);
    public abstract void deleteStore(Long storeId);
    public void existsStoreById(Long storeId);
}
