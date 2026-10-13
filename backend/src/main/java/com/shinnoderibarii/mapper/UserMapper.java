
package com.shinnoderibarii.mapper;

import com.shinnoderibarii.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("""
            SELECT
                id,
                name,
                email,
                password,
                phone,
                role,
                address,
                created_at AS createdAt,
                updated_at AS updatedAt
            FROM users
            WHERE email = #{email}
            LIMIT 1
            """)
    User findByEmail(String email);

    @Insert("""
            INSERT INTO users (
                name,
                email,
                password,
                role
            ) VALUES (
                #{name},
                #{email},
                #{password},
                #{role}
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
}
