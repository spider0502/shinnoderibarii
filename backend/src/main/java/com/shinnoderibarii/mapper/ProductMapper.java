package com.shinnoderibarii.mapper;

import com.shinnoderibarii.entity.Product;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductMapper {

    @Select("""
            SELECT
                id,
                store_id AS storeId,
                category_id AS categoryId,
                name,
                description,
                price,
                image_url AS imageUrl,
                status,
                sort_order AS sortOrder,
                created_at AS createdAt,
                updated_at AS updatedAt,
                deleted_at AS deletedAt
            FROM products
            WHERE store_id = #{storeId}
              AND deleted_at IS NULL
            ORDER BY category_id ASC, sort_order ASC, id ASC
            """)
    List<Product> findByStoreId(Long storeId);

    @Select("""
                SELECT
                    id,
                    store_id AS storeId,
                    category_id AS categoryId,
                    name,
                    description,
                    price,
                    image_url AS imageUrl,
                    status,
                    sort_order AS sortOrder,
                    created_at AS createdAt,
                    updated_at AS updatedAt,
                    deleted_at AS deletedAt
                FROM products
                WHERE id = #{productId}
                  AND deleted_at IS NULL
            """)
    Product findById(@Param("productId") Long productId);
}