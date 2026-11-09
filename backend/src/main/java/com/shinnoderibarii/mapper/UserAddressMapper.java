package com.shinnoderibarii.mapper;

import com.shinnoderibarii.entity.UserAddress;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserAddressMapper {

    @Select("""
                SELECT
                    id,
                    user_id AS userId,
                    recipient_name AS recipientName,
                    phone,
                    postal_code AS postalCode,
                    address,
                    is_default AS isDefault,
                    created_at AS createdAt,
                    updated_at AS updatedAt
                FROM user_addresses
                WHERE user_id = #{userId}
                ORDER BY is_default DESC, created_at DESC, id DESC
            """)
    List<UserAddress> findByUserId(Long userId);

    @Select("""
                SELECT
                    id,
                    user_id AS userId,
                    recipient_name AS recipientName,
                    phone,
                    postal_code AS postalCode,
                    address,
                    is_default AS isDefault,
                    created_at AS createdAt,
                    updated_at AS updatedAt
                FROM user_addresses
                WHERE id = #{addressId}
                  AND user_id = #{userId}
            """)
    UserAddress findByIdAndUserId(
            @Param("addressId") Long addressId,
            @Param("userId") Long userId);

    @Insert("""
                INSERT INTO user_addresses (
                    user_id,
                    recipient_name,
                    phone,
                    postal_code,
                    address,
                    is_default
                )
                VALUES (
                    #{userId},
                    #{recipientName},
                    #{phone},
                    #{postalCode},
                    #{address},
                    #{isDefault}
                )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserAddress userAddress);

    @Update("""
                UPDATE user_addresses
                SET
                    recipient_name = #{recipientName},
                    phone = #{phone},
                    postal_code = #{postalCode},
                    address = #{address}
                WHERE id = #{id}
                  AND user_id = #{userId}
            """)
    int updateByIdAndUserId(UserAddress userAddress);

    @Update("""
                UPDATE user_addresses
                SET is_default = FALSE
                WHERE user_id = #{userId}
            """)
    int clearDefaultByUserId(Long userId);

    @Update("""
                UPDATE user_addresses
                SET is_default = TRUE
                WHERE id = #{addressId}
                  AND user_id = #{userId}
            """)
    int setDefaultByIdAndUserId(
            @Param("addressId") Long addressId,
            @Param("userId") Long userId);

    @Delete("""
                DELETE FROM user_addresses
                WHERE id = #{addressId}
                  AND user_id = #{userId}
            """)
    int deleteByIdAndUserId(
            @Param("addressId") Long addressId,
            @Param("userId") Long userId);
}
