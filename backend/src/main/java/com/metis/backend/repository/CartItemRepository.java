package com.metis.backend.repository;

import com.metis.backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findAllByCart_IdOrderByIdAsc(Long cartId);

    Optional<CartItem> findByIdAndCart_Id(Long itemId, Long cartId);

    Optional<CartItem> findByCart_IdAndProduct_Id(Long cartId, Long productId);

    void deleteAllByCart_Id(Long cartId);
}
