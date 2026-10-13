
package com.shinnoderibarii.controller;

import com.shinnoderibarii.dto.UserRegisterRequest;
import com.shinnoderibarii.dto.UserRegisterResponse;
import com.shinnoderibarii.entity.User;
import com.shinnoderibarii.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserRegisterResponse register(
            @Valid @RequestBody UserRegisterRequest request) {

        User user = userService.register(request);

        return new UserRegisterResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole());
    }
}
