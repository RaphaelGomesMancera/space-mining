package com.spacemining.validator.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spacemining.validator.dto.CommandMessage;
import com.spacemining.validator.service.CommandPublisher;
import com.spacemining.validator.service.ValidationService;

@RestController
@RequestMapping("/validate")
public class ValidatorController {

    private final ValidationService validationService;
    private final CommandPublisher publisher;

    public ValidatorController(ValidationService validationService, CommandPublisher publisher) {
        this.validationService = validationService;
        this.publisher = publisher;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> validate(@RequestBody CommandMessage request) {
        validationService.validate(request.command());
        publisher.publish(request.command());
        return ResponseEntity.ok(Map.of("status", "VALID", "command", request.command()));
    }
}
