package com.evry.fruktkorgservice.service;

import com.evry.fruktkorgpersistence.model.Frukt;

import java.util.List;

public interface FruktService {
    void persist(Frukt frukt);
    void remove(Frukt frukt);
    Frukt merge(Frukt frukt);
    void refresh(Frukt frukt);
    List<Frukt> listFrukt();
}
