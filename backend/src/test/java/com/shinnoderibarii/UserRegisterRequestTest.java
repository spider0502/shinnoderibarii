
package com.shinnoderibarii;

import com.shinnoderibarii.dto.UserRegisterRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRegisterRequestTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        factory.close();
    }

    @Test
    void shouldRejectBlankName() {
        UserRegisterRequest request = new UserRegisterRequest(
                "",
                "test@example.com",
                "TestPass123!");

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectInvalidEmail() {
        UserRegisterRequest request = new UserRegisterRequest(
                "Test User",
                "invalid-email",
                "TestPass123!");

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldRejectShortPassword() {
        UserRegisterRequest request = new UserRegisterRequest(
                "Test User",
                "test@example.com",
                "123");

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void shouldAcceptValidRequest() {
        UserRegisterRequest request = new UserRegisterRequest(
                "Test User",
                "test@example.com",
                "TestPass123!");

        assertTrue(validator.validate(request).isEmpty());
    }
}
