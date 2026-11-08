package com.shinnoderibarii.mapper;

import com.shinnoderibarii.entity.CartItem;
import org.apache.ibatis.annotations.*;
import com.shinnoderibarii.dto.CartItemResponse;

import java.util.List;

@Mapper
public interface CartItemMapper {

    @Select("""
                SELECT
                    id,
                    cart_id AS cartId,
                    product_id AS productId,
                    quantity,
                    created_at AS createdAt,
                    updated_at AS updatedAt
                FROM cart_items
                WHERE cart_id = #{cartId}
                ORDER BY id ASC
            """)
    List<CartItem> findByCartId(Long cartId);

    @Select("""
                SELECT
                    id,
                    cart_id AS cartId,
                    product_id AS productId,
                    quantity,
                    created_at AS createdAt,
                    updated_at AS updatedAt
                FROM cart_items
                WHERE cart_id = #{cartId}
                  AND product_id = #{productId}
            """)
    CartItem findByCartIdAndProductId(
            @Param("cartId") Long cartId,
            @Param("productId") Long productId);

    @Insert("""
                INSERT INTO cart_items (
                    cart_id,
                    product_id,
                    quantity
                )
                VALUES (
                    #{cartId},
                    #{productId},
                    #{quantity}
                )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CartItem cartItem);

    @Update("""
                UPDATE cart_items
                SET quantity = #{quantity}
                WHERE id = #{id}
                  AND cart_id = #{cartId}
            """)
    int updateQuantity(
            @Param("id") Long id,
            @Param("cartId") Long cartId,
            @Param("quantity") Integer quantity);

    @Delete("""
                DELETE FROM cart_items
                WHERE id = #{id}
                  AND cart_id = #{cartId}
            """)
    int deleteById(
            @Param("id") Long id,
            @Param("cartId") Long cartId);

    @Select("""
            SELECT
                ci.id,
                ci.product_id AS productId,
                p.store_id AS storeId,
                s.name AS storeName,
                s.status AS storeStatus,
                p.name,
                p.description,
                p.price,
                p.image_url AS imageUrl,
                ci.quantity,
                p.price * ci.quantity AS subtotal
            FROM cart_items ci
            JOIN products p ON p.id = ci.product_id
            JOIN stores s ON s.id = p.store_id
            WHERE ci.cart_id = #{cartId}
              AND p.deleted_at IS NULL
            ORDER BY ci.id ASC
            """)
    List<CartItemResponse> findDetailsByCartId(
            @Param("cartId") Long cartId);
}