
package com.shinnoderibarii.service;

import com.shinnoderibarii.dto.UserRegisterRequest;
import com.shinnoderibarii.entity.User;
import com.shinnoderibarii.mapper.UserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shinnoderibarii.exception.EmailAlreadyExistsException;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Transactional
    public User register(UserRegisterRequest request) {
        User existingUser = userMapper.findByEmail(request.email());

        if (existingUser != null) {
            throw new EmailAlreadyExistsException();
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole("CUSTOMER");

        userMapper.insert(user);

        return user;
    }
}
