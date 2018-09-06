package com.evry.fruktkorg.domain.model;

public class Frukt {

    private long id;

    private String type;

    private int amount;

    private Fruktkorg fruktkorg;

    public Frukt() {
    }

    public Frukt(String type, int amount, Fruktkorg fruktkorg) {
        this.type = type;
        this.amount = amount;
        this.fruktkorg = fruktkorg;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Fruktkorg getFruktkorg() {
        return fruktkorg;
    }

    public void setFruktkorg(Fruktkorg fruktkorg) {
        this.fruktkorg = fruktkorg;
    }

    @Override
    public String toString() {
        return "Frukt{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", fruktkorg=" + fruktkorg.getName() +
                '}';
    }
}
