package com.evry.fruktkorgpersistence.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
@Table(name = "frukt")
public class Frukt {
    @Id
    @SequenceGenerator(name = "frukt_frukt_id_seq", sequenceName = "frukt_frukt_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "frukt_frukt_id_seq")
    @Column(name = "frukt_id", updatable = false)
    private long id;

    @Column(name = "type")
    private String type;

    @Column(name = "amount")
    private int amount;

    @OneToOne
    @JoinColumn(name = "fruktkorg_id")
    @JsonBackReference
    private Fruktkorg fruktkorg;

    public Frukt() {}

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
}
