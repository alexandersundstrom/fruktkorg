package com.evry.fruktkorgpersistence.dao;

import com.evry.fruktkorgpersistence.model.Frukt;

import javax.persistence.EntityManagerFactory;
import java.util.List;

public interface FruktDAO {
    void setEntityManagerFactory(EntityManagerFactory entityManagerFactory);

    void persist(Frukt frukt);
    void remove(long fruktId);
    Frukt merge(Frukt frukt);
    void refresh(Frukt frukt);

    List<Frukt> listFrukt();
}
