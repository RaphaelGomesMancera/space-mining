package com.spacemining.mining.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spacemining.mining.model.CommandCount;

public interface CommandCountRepository extends JpaRepository<CommandCount, String> {
}
