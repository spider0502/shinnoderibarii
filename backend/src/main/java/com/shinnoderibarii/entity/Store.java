
package com.shinnoderibarii.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Store {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String phone;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
