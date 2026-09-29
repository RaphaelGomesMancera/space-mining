package com.spacemining.mining.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "command_count")
public class CommandCount {

    @Id
    private String command;

    private long total;

    protected CommandCount() {
    }

    public CommandCount(String command) {
        this.command = command;
        this.total = 0;
    }

    public String getCommand() {
        return command;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
