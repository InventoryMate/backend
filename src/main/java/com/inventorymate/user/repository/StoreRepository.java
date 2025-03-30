package com.inventorymate.user.repository;

import com.inventorymate.user.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    boolean existsByUsername(String username);
    Optional<Store> findByUsername(String username);
}
