package com.spacemining.mining.service;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacemining.mining.model.CommandCount;
import com.spacemining.mining.repository.CommandCountRepository;

@Service
public class MiningService {

    private static final Logger log = LoggerFactory.getLogger(MiningService.class);

    private final CommandCountRepository repository;

    public MiningService(CommandCountRepository repository) {
        this.repository = repository;
    }

    // Executa o comando no robô (log) e incrementa a contagem no banco
    @Transactional
    public void registerCommand(String command) {
        log.info("Robô executando comando: {}", command);

        CommandCount count = repository.findById(command)
                .orElseGet(() -> new CommandCount(command));
        count.setTotal(count.getTotal() + 1);
        repository.save(count);

        log.info("Contagem do comando {}: {}", command, count.getTotal());
    }

    // Formato do enunciado: { "LEFT": 10, "FRONT": 40, "OPEN": 8, ... }
    @Transactional(readOnly = true)
    public Map<String, Long> getCounts() {
        Map<String, Long> counts = new TreeMap<>();
        for (CommandCount count : repository.findAll()) {
            counts.put(count.getCommand(), count.getTotal());
        }
        return counts;
    }
}
