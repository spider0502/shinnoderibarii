package com.shinnoderibarii.service;

import com.shinnoderibarii.entity.UserAddress;
import com.shinnoderibarii.mapper.UserAddressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAddressService {

    private final UserAddressMapper userAddressMapper;

    /**
     * 获取用户保存的全部配送地址。
     */
    @Transactional(readOnly = true)
    public List<UserAddress> getAddresses(Long userId) {
        return userAddressMapper.findByUserId(userId);
    }

    /**
     * 新增配送地址。用户的第一个地址自动设为默认地址。
     */
    @Transactional
    public UserAddress addAddress(Long userId, UserAddress userAddress) {
        List<UserAddress> addresses = userAddressMapper.findByUserId(userId);

        userAddress.setUserId(userId);
        userAddress.setIsDefault(addresses.isEmpty());

        userAddressMapper.insert(userAddress);

        return userAddress;
    }

    /**
     * 修改配送地址的收件人及地址信息。
     */
    @Transactional
    public void updateAddress(
            Long userId,
            Long addressId,
            UserAddress userAddress) {

        UserAddress existingAddress = userAddressMapper.findByIdAndUserId(addressId, userId);

        if (existingAddress == null) {
            throw new IllegalArgumentException("配送地址不存在");
        }

        userAddress.setId(addressId);
        userAddress.setUserId(userId);

        int updated = userAddressMapper.updateByIdAndUserId(userAddress);

        if (updated == 0) {
            throw new IllegalArgumentException("配送地址の更新に失敗しました");
        }
    }

    /**
     * 删除配送地址。如果删除的是默认地址，则将剩余地址中的第一个设为默认地址。
     */
    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        UserAddress address = userAddressMapper.findByIdAndUserId(addressId, userId);

        if (address == null) {
            throw new IllegalArgumentException("配送地址不存在");
        }

        int deleted = userAddressMapper.deleteByIdAndUserId(addressId, userId);

        if (deleted == 0) {
            throw new IllegalArgumentException("配送地址の削除に失敗しました");
        }

        if (Boolean.TRUE.equals(address.getIsDefault())) {
            List<UserAddress> remainingAddresses = userAddressMapper.findByUserId(userId);

            if (!remainingAddresses.isEmpty()) {
                Long newDefaultAddressId = remainingAddresses.get(0).getId();

                userAddressMapper.setDefaultByIdAndUserId(
                        newDefaultAddressId,
                        userId);
            }
        }
    }

    /**
     * 将指定地址设为默认地址。
     */
    @Transactional
    public void setDefaultAddress(Long userId, Long addressId) {
        UserAddress address = userAddressMapper.findByIdAndUserId(addressId, userId);

        if (address == null) {
            throw new IllegalArgumentException("配送地址不存在");
        }

        userAddressMapper.clearDefaultByUserId(userId);

        int updated = userAddressMapper.setDefaultByIdAndUserId(
                addressId,
                userId);

        if (updated == 0) {
            throw new IllegalArgumentException("默认配送地址の設定に失敗しました");
        }
    }
}
