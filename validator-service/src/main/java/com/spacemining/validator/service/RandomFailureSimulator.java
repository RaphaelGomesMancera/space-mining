package com.spacemining.validator.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// Simula falha de comunicação/validação com 50% de chance (configurável)
@Component
public class RandomFailureSimulator implements FailureSimulator {

    private final Random random = new Random();
    private final double failureRate;

    public RandomFailureSimulator(@Value("${validator.failure-rate:0.5}") double failureRate) {
        this.failureRate = failureRate;
    }

    @Override
    public boolean shouldFail() {
        return random.nextDouble() < failureRate;
    }
}
