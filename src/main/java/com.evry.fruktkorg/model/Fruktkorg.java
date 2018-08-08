package com.evry.fruktkorg.model;

import javax.persistence.*;

@Entity
@Table(name = "fruktkorg")
public class Fruktkorg {
    @Id
    @SequenceGenerator(name = "fruktkorg_fruktkorg_id_seq", sequenceName = "fruktkorg_fruktkorg_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fruktkorg_fruktkorg_id_seq")
    @Column(name = "fruktkorg_id", updatable = false)
    private long id;

    @Column(name = "name")
    private String name;

    public Fruktkorg() {}

    public Fruktkorg(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }

}
