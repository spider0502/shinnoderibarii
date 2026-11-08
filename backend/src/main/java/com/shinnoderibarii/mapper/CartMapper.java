package com.shinnoderibarii.mapper;

import com.shinnoderibarii.entity.Cart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CartMapper {

    @Select("""
                SELECT
                    id,
                    user_id AS userId,
                    created_at AS createdAt,
                    updated_at AS updatedAt
                FROM carts
                WHERE user_id = #{userId}
            """)
    Cart findByUserId(Long userId);

    @Insert("""
                INSERT INTO carts (user_id)
                VALUES (#{userId})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Cart cart);
}