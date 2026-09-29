package com.spacemining.command.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.spacemining.command.dto.CommandRequest;
import com.spacemining.command.exception.TooManyRequestsException;

@ExtendWith(MockitoExtension.class)
class CommandServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Test
    void sendsCommandToValidatorService() {
        CommandService service = new CommandService(restTemplate, 5);

        service.send(new CommandRequest("LEFT"));

        verify(restTemplate).postForEntity(
                eq("http://validator-service/validate"), any(CommandRequest.class), eq(Void.class));
    }

    @Test
    void rejectsCommandWhenLimitIsReached() {
        CommandService service = new CommandService(restTemplate, 0);

        assertThrows(TooManyRequestsException.class, () -> service.send(new CommandRequest("LEFT")));
    }
}
