package com.shinnoderibarii.mapper;

import com.shinnoderibarii.entity.Category;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CategoryMapper {

    @Select("""
            SELECT
                id,
                store_id AS storeId,
                name,
                sort_order AS sortOrder,
                created_at AS createdAt,
                updated_at AS updatedAt,
                deleted_at AS deletedAt
            FROM categories
            WHERE store_id = #{storeId}
              AND deleted_at IS NULL
            ORDER BY sort_order ASC, id ASC
            """)
    List<Category> findByStoreId(Long storeId);
}