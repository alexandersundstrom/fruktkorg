package com.evry.fruktkorgservice.utils.builders;

import com.evry.fruktkorgservice.domain.model.ImmutableFrukt;

public class ImmutableFruktBuilder {
    private long id;
    private String type;
    private int amount;
    private long fruktkorgId;

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

    public ImmutableFruktBuilder setFruktkorgId(long fruktkorgId) {
        this.fruktkorgId = fruktkorgId;
        return this;
    }

    public ImmutableFrukt createImmutableFrukt() {
        return new ImmutableFrukt(id, type, amount, fruktkorgId);
    }
}