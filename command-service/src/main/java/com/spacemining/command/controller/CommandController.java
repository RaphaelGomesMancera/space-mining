package com.spacemining.command.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spacemining.command.dto.CommandRequest;
import com.spacemining.command.service.CommandService;

@RestController
@RequestMapping("/command")
public class CommandController {

    private final CommandService commandService;

    public CommandController(CommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> receive(@RequestBody CommandRequest request) {
        commandService.send(request);
        return ResponseEntity.accepted().body(Map.of(
                "status", "ACCEPTED",
                "command", String.valueOf(request.command())));
    }
}
