package com.shinnoderibarii.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAddress {

    private Long id;

    private Long userId;

    private String recipientName;

    private String phone;

    private String postalCode;

    private String address;

    private Boolean isDefault;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}