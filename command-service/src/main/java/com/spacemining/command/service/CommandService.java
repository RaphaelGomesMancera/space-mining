package com.spacemining.command.service;

import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.spacemining.command.dto.CommandRequest;
import com.spacemining.command.exception.TooManyRequestsException;

@Service
public class CommandService {

    private static final Logger log = LoggerFactory.getLogger(CommandService.class);
    private static final String VALIDATOR_URL = "http://validator-service/validate";

    private final RestTemplate restTemplate;
    // Backpressure na entrada: limita quantos comandos ficam "em voo" ao mesmo tempo
    private final Semaphore inFlight;

    public CommandService(RestTemplate restTemplate,
                          @Value("${command.max-concurrent-requests:20}") int maxConcurrentRequests) {
        this.restTemplate = restTemplate;
        this.inFlight = new Semaphore(maxConcurrentRequests);
    }

    public void send(CommandRequest request) {
        if (!inFlight.tryAcquire()) {
            log.warn("Limite de comandos simultâneos atingido, rejeitando {}", request.command());
            throw new TooManyRequestsException("Muitos comandos em processamento, tente novamente em instantes");
        }
        try {
            log.info("Enviando comando {} para o validator-service", request.command());
            restTemplate.postForEntity(VALIDATOR_URL, request, Void.class);
            log.info("Comando {} aceito pelo validator-service", request.command());
        } finally {
            inFlight.release();
        }
    }
}
