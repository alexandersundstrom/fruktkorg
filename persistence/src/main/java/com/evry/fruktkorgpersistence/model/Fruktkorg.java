package com.evry.fruktkorgpersistence.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "fruktkorg", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Frukt> fruktList = new ArrayList<>();

    public Fruktkorg() {}

    public Fruktkorg(String name) {
        this.name = name;
    }

    public List<Frukt> getFruktList() {
        return fruktList;
    }

    public void setFruktList(List<Frukt> fruktList) {
        this.fruktList = fruktList;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Fruktkorg{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", fruktList=" + fruktList +
                '}';
    }
}
