package com.shinnoderibarii.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Category {

    private Long id;
    private Long storeId;
    private String name;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}