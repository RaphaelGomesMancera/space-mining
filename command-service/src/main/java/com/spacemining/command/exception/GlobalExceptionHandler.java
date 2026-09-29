package com.spacemining.command.exception;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // validator respondeu 4xx (ex.: comando inválido)
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Map<String, String>> handleClientError(HttpClientErrorException e) {
        log.warn("Comando rejeitado pelo validator: {}", e.getStatusCode());
        return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                "error", "Comando inválido. Use RIGHT, LEFT, FRONT, BACK, OPEN ou CLOSE"));
    }

    // validator falhou (5xx), está fora do ar (timeout/conexão) ou sem instância no Eureka
    @ExceptionHandler({HttpServerErrorException.class, ResourceAccessException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleUnavailable(Exception e) {
        log.error("Serviço de validação indisponível: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "error", "Serviço de validação indisponível, tente novamente"));
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<Map<String, String>> handleTooMany(TooManyRequestsException e) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("error", e.getMessage()));
    }
}
