package com.spacemining.validator.model;

public enum CommandType {
    RIGHT, LEFT, FRONT, BACK, OPEN, CLOSE;

    public static boolean isValid(String value) {
        if (value == null) {
            return false;
        }
        for (CommandType type : values()) {
            if (type.name().equals(value)) {
                return true;
            }
        }
        return false;
    }
}
