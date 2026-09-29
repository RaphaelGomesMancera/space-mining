package com.spacemining.validator.service;

@FunctionalInterface
public interface FailureSimulator {

    boolean shouldFail();
}
