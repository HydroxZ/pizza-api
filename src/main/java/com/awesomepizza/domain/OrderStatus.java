package com.awesomepizza.domain;

public enum OrderStatus {
    PENDING("PENDING", 0),
    COOKING("COOKING", 1),
    READY("READY", 2),
    CANCELLED("CANCELLED", 3);

    private final String name;
    private final int priority;

    OrderStatus(String name, int priority) {
        this.name = name;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }
}
