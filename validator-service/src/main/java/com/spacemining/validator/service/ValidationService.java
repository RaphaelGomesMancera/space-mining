package com.spacemining.validator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import com.spacemining.validator.exception.InvalidCommandException;
import com.spacemining.validator.exception.ValidationFailureException;
import com.spacemining.validator.model.CommandType;

@Service
public class ValidationService {

    private static final Logger log = LoggerFactory.getLogger(ValidationService.class);

    private final RetryTemplate retryTemplate;
    private final FailureSimulator failureSimulator;

    public ValidationService(RetryTemplate retryTemplate, FailureSimulator failureSimulator) {
        this.retryTemplate = retryTemplate;
        this.failureSimulator = failureSimulator;
    }

    public void validate(String command) {
        // comando inválido não adianta tentar de novo: falha na hora (400)
        if (!CommandType.isValid(command)) {
            throw new InvalidCommandException(
                    "Comando inválido: " + command + ". Use RIGHT, LEFT, FRONT, BACK, OPEN ou CLOSE");
        }

        // falha simulada: tenta de novo com exponential backoff; esgotando, lança ValidationFailureException
        retryTemplate.execute((RetryCallback<Void, RuntimeException>) context -> {
            log.info("Validando comando {} (tentativa {})", command, context.getRetryCount() + 1);

            if (failureSimulator.shouldFail()) {
                log.warn("Falha simulada na validação do comando {}, aguardando backoff para tentar novamente", command);
                throw new ValidationFailureException("Falha na validação do comando " + command);
            }

            log.info("Comando {} validado com sucesso", command);
            return null;
        });
    }
}
