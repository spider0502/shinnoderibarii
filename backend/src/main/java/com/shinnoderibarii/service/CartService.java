package com.shinnoderibarii.service;

import com.shinnoderibarii.entity.Cart;
import com.shinnoderibarii.entity.CartItem;
import com.shinnoderibarii.entity.Product;
import com.shinnoderibarii.mapper.CartItemMapper;
import com.shinnoderibarii.mapper.CartMapper;
import com.shinnoderibarii.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shinnoderibarii.dto.CartItemResponse;
import com.shinnoderibarii.dto.CartResponse;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final ProductMapper productMapper;

    /**
     * 获取用户购物车，不存在时自动创建。
     */
    @Transactional
    public Cart getOrCreateCart(Long userId) {
        Cart cart = cartMapper.findByUserId(userId);

        if (cart != null) {
            return cart;
        }

        cart = new Cart();
        cart.setUserId(userId);
        cartMapper.insert(cart);

        return cart;
    }

    /**
     * 添加商品到购物车。
     */
    @Transactional
    public void addItem(Long userId, Long productId, Integer quantity) {

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }

        Product product = productMapper.findById(productId);

        if (product == null) {
            throw new IllegalArgumentException("商品不存在");
        }

        if (!"ON_SALE".equals(product.getStatus())) {
            throw new IllegalArgumentException("该商品目前无法购买");
        }

        Cart cart = getOrCreateCart(userId);

        CartItem existingItem = cartItemMapper.findByCartIdAndProductId(
                cart.getId(),
                productId);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + quantity;

            cartItemMapper.updateQuantity(
                    existingItem.getId(),
                    cart.getId(),
                    newQuantity);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCartId(cart.getId());
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);

            cartItemMapper.insert(cartItem);
        }
    }

    /**
     * 获取用户购物车中的商品详情。
     */
    @Transactional(readOnly = true)
    public List<CartItemResponse> getCartItems(Long userId) {

        Cart cart = cartMapper.findByUserId(userId);

        if (cart == null) {
            return List.of();
        }

        return cartItemMapper.findDetailsByCartId(cart.getId());
    }

    /**
     * 修改购物车商品数量。
     */
    @Transactional
    public void updateItemQuantity(
            Long userId,
            Long cartItemId,
            Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("数量必须大于0");
        }

        Cart cart = cartMapper.findByUserId(userId);

        if (cart == null) {
            throw new IllegalArgumentException("购物车不存在");
        }

        int updated = cartItemMapper.updateQuantity(
                cartItemId,
                cart.getId(),
                quantity);

        if (updated == 0) {
            throw new IllegalArgumentException("购物车商品不存在");
        }
    }

    /**
     * 删除购物车商品。
     */
    @Transactional
    public void removeItem(Long userId, Long cartItemId) {

        Cart cart = cartMapper.findByUserId(userId);

        if (cart == null) {
            throw new IllegalArgumentException("购物车不存在");
        }

        int deleted = cartItemMapper.deleteById(
                cartItemId,
                cart.getId());

        if (deleted == 0) {
            throw new IllegalArgumentException("购物车商品不存在");
        }
    }

    /**
     * 获取购物车完整信息。
     */
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {

        List<CartItemResponse> items = getCartItems(userId);

        BigDecimal totalAmount = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (CartItemResponse item : items) {
            totalAmount = totalAmount.add(item.getSubtotal());
            totalQuantity += item.getQuantity();
        }

        CartResponse response = new CartResponse();
        response.setItems(items);
        response.setTotalAmount(totalAmount);
        response.setTotalQuantity(totalQuantity);

        return response;
    }
}