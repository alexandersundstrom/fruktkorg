package com.evry.fruktkorgservice.model;

public class ImmutableFrukt {
    private long id;
    private String type;
    private int amount;
    private long fruktkorgId;

    ImmutableFrukt(long id, String type, int amount, long fruktkorgId) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.fruktkorgId = fruktkorgId;
    }

    public String getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }

    public long getFruktkorgId() {
        return fruktkorgId;
    }

    public long getId() {
        return id;
    }
}
