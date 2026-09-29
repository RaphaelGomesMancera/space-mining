package com.spacemining.mining.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.spacemining.mining.config.RabbitConfig;
import com.spacemining.mining.dto.CommandMessage;
import com.spacemining.mining.service.MiningService;

@Component
public class CommandConsumer {

    private static final Logger log = LoggerFactory.getLogger(CommandConsumer.class);

    private final MiningService miningService;
    private final long processingDelayMs;

    public CommandConsumer(MiningService miningService,
                           @Value("${mining.processing-delay-ms:300}") long processingDelayMs) {
        this.miningService = miningService;
        this.processingDelayMs = processingDelayMs;
    }

    // Backpressure: prefetch=1 e concurrency=1 (application.yml) => o robô recebe 1 ordem por vez
    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void receive(CommandMessage message) {
        log.info("Ordem recebida da fila: {}", message.command());
        miningService.registerCommand(message.command());
        simulateRobotWork();
    }

    private void simulateRobotWork() {
        try {
            Thread.sleep(processingDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
