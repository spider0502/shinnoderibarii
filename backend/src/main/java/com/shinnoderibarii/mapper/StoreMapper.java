
package com.shinnoderibarii.mapper;

import com.shinnoderibarii.entity.Store;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface StoreMapper {

    @Select("""
            SELECT
                id,
                name,
                description,
                address,
                phone,
                status,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM stores
            ORDER BY id
            """)
    List<Store> findAll();

    // @Select("""
    // SELECT
    // @@character_set_client AS clientCharset,
    // @@character_set_connection AS connectionCharset,
    // @@character_set_results AS resultsCharset,
    // @@character_set_database AS databaseCharset
    // """)
    // Map<String, String> getCharsetInfo();

    // @Select("""
    // SELECT
    // name,
    // HEX(name) AS nameHex,
    // LENGTH(name) AS byteLength,
    // CHAR_LENGTH(name) AS charLength
    // FROM stores
    // ORDER BY id
    // """)
    // List<Map<String, Object>> inspectStoreNames();
}
