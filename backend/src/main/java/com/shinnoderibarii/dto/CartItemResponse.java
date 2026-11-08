package com.shinnoderibarii.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemResponse {

    private Long id;

    private Long productId;

    private String name;

    private String description;

    private BigDecimal price;

    private String imageUrl;

    private Integer quantity;

    private BigDecimal subtotal;

    private Long storeId;
    private String storeName;
    private String storeStatus;
}