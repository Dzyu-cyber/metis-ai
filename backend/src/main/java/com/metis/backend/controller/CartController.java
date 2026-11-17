package com.metis.backend.controller;

import com.metis.backend.dto.AddCartItemRequest;
import com.metis.backend.dto.UpdateCartItemRequest;
import com.metis.backend.dto.CartResponse;

import com.metis.backend.service.CartService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse getCart(Authentication authentication) {

        return cartService.getCart(authentication);
    }

    @PostMapping("/items")
    public CartResponse addItem(
            @Valid @RequestBody AddCartItemRequest request,
            Authentication authentication) {

        return cartService.addItem(request, authentication);
    }

    @PutMapping("/items/{itemId}")
    public CartResponse updateItem(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request,
            Authentication authentication) {

        return cartService.updateItem(
                itemId,
                request,
                authentication
        );
    }

    @DeleteMapping("/items/{itemId}")
    public CartResponse removeItem(
            @PathVariable Long itemId,
            Authentication authentication) {

        return cartService.removeItem(itemId, authentication);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            Authentication authentication) {

        cartService.clearCart(authentication);

        return ResponseEntity.noContent().build();
    }
}
