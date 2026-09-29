package com.spacemining.validator.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.retry.support.RetryTemplate;

import com.spacemining.validator.exception.InvalidCommandException;
import com.spacemining.validator.exception.ValidationFailureException;
import com.spacemining.validator.model.CommandType;

class ValidationServiceTest {

    private RetryTemplate fastRetry(int maxAttempts) {
        return RetryTemplate.builder()
                .maxAttempts(maxAttempts)
                .fixedBackoff(1)
                .retryOn(ValidationFailureException.class)
                .build();
    }

    @Test
    void acceptsAllSixCommands() {
        ValidationService service = new ValidationService(fastRetry(5), () -> false);

        for (CommandType type : CommandType.values()) {
            assertDoesNotThrow(() -> service.validate(type.name()));
        }
        assertEquals(6, CommandType.values().length);
    }

    @Test
    void rejectsInvalidCommandsWithoutRetry() {
        AtomicInteger calls = new AtomicInteger();
        ValidationService service = new ValidationService(fastRetry(5), () -> {
            calls.incrementAndGet();
            return false;
        });

        assertThrows(InvalidCommandException.class, () -> service.validate("JUMP"));
        assertThrows(InvalidCommandException.class, () -> service.validate("left"));
        assertThrows(InvalidCommandException.class, () -> service.validate(null));
        assertEquals(0, calls.get());
    }

    @Test
    void retriesUntilValidationSucceeds() {
        AtomicInteger calls = new AtomicInteger();
        // falha nas 2 primeiras tentativas e passa na 3ª
        ValidationService service = new ValidationService(fastRetry(5), () -> calls.incrementAndGet() < 3);

        assertDoesNotThrow(() -> service.validate("LEFT"));
        assertEquals(3, calls.get());
    }

    @Test
    void failsAfterMaxAttempts() {
        AtomicInteger calls = new AtomicInteger();
        ValidationService service = new ValidationService(fastRetry(5), () -> {
            calls.incrementAndGet();
            return true;
        });

        assertThrows(ValidationFailureException.class, () -> service.validate("FRONT"));
        assertEquals(5, calls.get());
    }

    @Test
    void commandTypeValidation() {
        assertTrue(CommandType.isValid("OPEN"));
        assertFalse(CommandType.isValid("open"));
        assertFalse(CommandType.isValid(null));
    }
}
