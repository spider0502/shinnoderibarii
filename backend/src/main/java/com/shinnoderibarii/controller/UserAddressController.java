package com.shinnoderibarii.controller;

import com.shinnoderibarii.dto.UserAddressRequest;
import com.shinnoderibarii.entity.UserAddress;
import com.shinnoderibarii.security.CustomUserPrincipal;
import com.shinnoderibarii.service.UserAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService userAddressService;

    /**
     * 获取当前用户的全部配送地址。
     */
    @GetMapping
    public ResponseEntity<List<UserAddress>> getAddresses(
            Authentication authentication) {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        return ResponseEntity.ok(
                userAddressService.getAddresses(principal.getUserId()));
    }

    /**
     * 添加配送地址。
     */
    @PostMapping
    public ResponseEntity<Void> addAddress(
            Authentication authentication,
            @Valid @RequestBody UserAddressRequest request) {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        UserAddress userAddress = new UserAddress();
        userAddress.setRecipientName(request.getRecipientName());
        userAddress.setPhone(request.getPhone());
        userAddress.setPostalCode(request.getPostalCode());
        userAddress.setAddress(request.getAddress());

        userAddressService.addAddress(principal.getUserId(), userAddress);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 修改配送地址。
     */
    @PutMapping("/{addressId}")
    public ResponseEntity<Void> updateAddress(
            Authentication authentication,
            @PathVariable Long addressId,
            @Valid @RequestBody UserAddressRequest request) {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        UserAddress userAddress = new UserAddress();
        userAddress.setRecipientName(request.getRecipientName());
        userAddress.setPhone(request.getPhone());
        userAddress.setPostalCode(request.getPostalCode());
        userAddress.setAddress(request.getAddress());

        userAddressService.updateAddress(
                principal.getUserId(),
                addressId,
                userAddress);

        return ResponseEntity.noContent().build();
    }

    /**
     * 删除配送地址。
     */
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            Authentication authentication,
            @PathVariable Long addressId) {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        userAddressService.deleteAddress(principal.getUserId(), addressId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 将指定地址设为默认地址。
     */
    @PutMapping("/{addressId}/default")
    public ResponseEntity<Void> setDefaultAddress(
            Authentication authentication,
            @PathVariable Long addressId) {

        CustomUserPrincipal principal = (CustomUserPrincipal) authentication.getPrincipal();

        userAddressService.setDefaultAddress(
                principal.getUserId(),
                addressId);

        return ResponseEntity.noContent().build();
    }
}
