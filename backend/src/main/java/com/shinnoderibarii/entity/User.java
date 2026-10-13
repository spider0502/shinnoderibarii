
package com.shinnoderibarii.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {

    private Long id;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String role;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
