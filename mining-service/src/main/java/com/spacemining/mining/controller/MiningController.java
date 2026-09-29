package com.spacemining.mining.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spacemining.mining.service.MiningService;

@RestController
@RequestMapping("/commands")
public class MiningController {

    private final MiningService miningService;

    public MiningController(MiningService miningService) {
        this.miningService = miningService;
    }

    @GetMapping
    public Map<String, Long> counts() {
        return miningService.getCounts();
    }
}
