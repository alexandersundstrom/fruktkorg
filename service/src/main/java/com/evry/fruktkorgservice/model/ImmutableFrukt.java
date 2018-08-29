package com.evry.fruktkorgservice.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "frukt")
public class ImmutableFrukt {
    @XmlElement(name = "id")
    private long id;
    @XmlElement(name = "type")
    private String type;
    @XmlElement(name = "amount")
    private int amount;
    private long fruktkorgId;

    private ImmutableFrukt() {}

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

    @Override
    public String toString() {
        return "ImmutableFrukt{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", fruktkorgId=" + fruktkorgId +
                '}';
    }
}
