package com.evershine.EvershineServer.CartApi.repository;

import com.evershine.EvershineServer.CartApi.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    @Query("SELECT c FROM Cart c " +
            "JOIN FETCH c.product p " +
            "LEFT JOIN FETCH p.brand b " +
            "WHERE c.appUser.id = :appUserId")
    List<Cart> findCartWithProducts(@Param("appUserId") UUID appUserId);

    List<Cart> findByAppUser_id(UUID userId);

    // Optimized query (single SQL query with product and brand fetched)
    @Query("SELECT c FROM Cart c " +
            "JOIN FETCH c.product p " +
            "LEFT JOIN FETCH p.brand b " +
            "WHERE c.appUser.id = :userId")
    List<Cart> findAllByUserIdWithProduct(@Param("userId") UUID userId);
}
