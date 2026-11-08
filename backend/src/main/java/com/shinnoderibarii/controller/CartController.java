package com.shinnoderibarii.controller;

import com.shinnoderibarii.dto.CartItemQuantityRequest;
import com.shinnoderibarii.dto.CartItemRequest;
import com.shinnoderibarii.dto.CartResponse;
import com.shinnoderibarii.security.CustomUserPrincipal;
import com.shinnoderibarii.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 获取当前用户的购物车。
     */
    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            Authentication authentication) {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        Long userId = principal.getUserId();
        // String email = principal.getEmail();

        return ResponseEntity.ok(cartService.getCart(userId));
    }

    /**
     * 添加商品到购物车。
     */
    @PostMapping("/items")
    public ResponseEntity<Void> addItem(
            Authentication authentication,
            @Valid @RequestBody CartItemRequest request) {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        cartService.addItem(
                principal.getUserId(),
                request.getProductId(),
                request.getQuantity());

        return ResponseEntity.noContent().build();
    }

    /**
     * 修改购物车商品数量。
     */
    @PutMapping("/items/{cartItemId}")
    public ResponseEntity<Void> updateItemQuantity(
            Authentication authentication,
            @PathVariable Long cartItemId,
            @Valid @RequestBody CartItemQuantityRequest request) {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        cartService.updateItemQuantity(
                principal.getUserId(),
                cartItemId,
                request.getQuantity());

        return ResponseEntity.noContent().build();
    }

    /**
     * 删除购物车商品。
     */
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeItem(
            Authentication authentication,
            @PathVariable Long cartItemId) {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        cartService.removeItem(
                principal.getUserId(),
                cartItemId);

        return ResponseEntity.noContent().build();
    }
}