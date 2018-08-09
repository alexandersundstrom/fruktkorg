package com.evry.fruktkorgservice.model;

public class ImmutableFrukt {
    private long id;
    private String type;
    private int amount;

    ImmutableFrukt(long id, String type, int amount) {
        this.id = id;
        this.type = type;
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public long getId() {
        return id;
    }
}
