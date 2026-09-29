package com.spacemining.validator.exception;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidCommandException.class)
    public ResponseEntity<Map<String, String>> handleInvalid(InvalidCommandException e) {
        log.warn(e.getMessage());
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }

    // todas as tentativas de validação falharam
    @ExceptionHandler(ValidationFailureException.class)
    public ResponseEntity<Map<String, String>> handleFailure(ValidationFailureException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "error", "Validação falhou após todas as tentativas"));
    }

    // RabbitMQ indisponível na hora de publicar
    @ExceptionHandler(AmqpException.class)
    public ResponseEntity<Map<String, String>> handleAmqp(AmqpException e) {
        log.error("Falha ao publicar no RabbitMQ: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Map.of(
                "error", "Fila indisponível, tente novamente"));
    }
}
