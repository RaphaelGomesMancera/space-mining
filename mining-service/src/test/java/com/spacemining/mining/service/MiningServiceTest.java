package com.spacemining.mining.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.spacemining.mining.model.CommandCount;
import com.spacemining.mining.repository.CommandCountRepository;

@ExtendWith(MockitoExtension.class)
class MiningServiceTest {

    @Mock
    private CommandCountRepository repository;

    @InjectMocks
    private MiningService service;

    @Test
    void incrementsExistingCommandCount() {
        CommandCount existing = new CommandCount("LEFT");
        existing.setTotal(9);
        when(repository.findById("LEFT")).thenReturn(Optional.of(existing));

        service.registerCommand("LEFT");

        assertEquals(10, existing.getTotal());
        verify(repository).save(existing);
    }

    @Test
    void createsCountForFirstOccurrence() {
        when(repository.findById("OPEN")).thenReturn(Optional.empty());

        service.registerCommand("OPEN");

        ArgumentCaptor<CommandCount> captor = ArgumentCaptor.forClass(CommandCount.class);
        verify(repository).save(captor.capture());
        assertEquals("OPEN", captor.getValue().getCommand());
        assertEquals(1, captor.getValue().getTotal());
    }

    @Test
    void returnsCountsInExpectedFormat() {
        CommandCount left = new CommandCount("LEFT");
        left.setTotal(10);
        CommandCount front = new CommandCount("FRONT");
        front.setTotal(40);
        when(repository.findAll()).thenReturn(List.of(left, front));

        Map<String, Long> counts = service.getCounts();

        assertEquals(Map.of("LEFT", 10L, "FRONT", 40L), counts);
    }
}
