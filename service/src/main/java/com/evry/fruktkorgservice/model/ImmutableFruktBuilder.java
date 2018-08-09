package com.evry.fruktkorgservice.model;

public class ImmutableFruktBuilder {
    private long id;
    private String type;
    private int amount;

    public ImmutableFruktBuilder setId(long id) {
        this.id = id;
        return this;
    }

    public ImmutableFruktBuilder setType(String type) {
        this.type = type;
        return this;
    }

    public ImmutableFruktBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ImmutableFrukt createImmutableFrukt() {
        return new ImmutableFrukt(id, type, amount);
    }
}