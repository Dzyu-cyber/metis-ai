package com.metis.backend.service;

import com.metis.backend.dto.AddCartItemRequest;
import com.metis.backend.dto.UpdateCartItemRequest;
import com.metis.backend.dto.CartItemResponse;
import com.metis.backend.dto.CartResponse;

import com.metis.backend.entity.AppUser;
import com.metis.backend.entity.Cart;
import com.metis.backend.entity.CartItem;
import com.metis.backend.entity.Product;

import com.metis.backend.exception.ResourceNotFoundException;

import com.metis.backend.repository.CartItemRepository;
import com.metis.backend.repository.CartRepository;
import com.metis.backend.repository.ProductRepository;
import com.metis.backend.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CartResponse getCart(Authentication authentication) {

        Cart cart = getOrCreateCart(authentication);

        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse addItem(
            AddCartItemRequest request,
            Authentication authentication) {

        Cart cart = getOrCreateCart(authentication);

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        CartItem item = cartItemRepository
                .findByCart_IdAndProduct_Id(cart.getId(), product.getId())
                .orElse(null);

        int newQuantity = request.quantity();

        if (item != null) {
            newQuantity += item.getQuantity();
        }

        checkStock(product, newQuantity);

        if (item == null) {
            item = new CartItem(cart, product, newQuantity);
        } else {
            item.setQuantity(newQuantity);
        }

        cartItemRepository.save(item);

        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(
            Long itemId,
            UpdateCartItemRequest request,
            Authentication authentication) {

        Cart cart = getOrCreateCart(authentication);

        CartItem item = cartItemRepository
                .findByIdAndCart_Id(itemId, cart.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found"));

        checkStock(item.getProduct(), request.quantity());

        item.setQuantity(request.quantity());

        cartItemRepository.save(item);

        return buildCartResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(
            Long itemId,
            Authentication authentication) {

        Cart cart = getOrCreateCart(authentication);

        CartItem item = cartItemRepository
                .findByIdAndCart_Id(itemId, cart.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found"));

        cartItemRepository.delete(item);

        return buildCartResponse(cart);
    }

    @Transactional
    public void clearCart(Authentication authentication) {

        Cart cart = getOrCreateCart(authentication);

        cartItemRepository.deleteAllByCart_Id(cart.getId());
    }

    private Cart getOrCreateCart(Authentication authentication) {

        AppUser user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return cartRepository.findByUser_Id(user.getId())
                .orElseGet(() -> cartRepository.save(new Cart(user)));
    }

    private void checkStock(Product product, Integer requestedQuantity) {

        if (requestedQuantity > product.getStockQuantity()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Requested quantity exceeds available stock"
            );
        }
    }

    private CartResponse buildCartResponse(Cart cart) {

        List<CartItemResponse> items = cartItemRepository
                .findAllByCart_IdOrderByIdAsc(cart.getId())
                .stream()
                .map(this::toCartItemResponse)
                .toList();

        int totalItems = items.stream()
                .mapToInt(CartItemResponse::quantity)
                .sum();

        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(
                items,
                totalItems,
                subtotal
        );
    }

    private CartItemResponse toCartItemResponse(CartItem item) {

        Product product = item.getProduct();

        BigDecimal lineTotal = product.getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartItemResponse(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getPrice(),
                item.getQuantity(),
                lineTotal
        );
    }
}
