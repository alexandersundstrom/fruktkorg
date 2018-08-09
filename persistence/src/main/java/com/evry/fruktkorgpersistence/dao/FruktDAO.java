package com.evry.fruktkorgpersistence.dao;

import com.evry.fruktkorgpersistence.model.Frukt;

import javax.persistence.EntityManager;
import java.util.List;

public interface FruktDAO {
    void setEntityManager(EntityManager entityManager);

    void persist(Frukt frukt);
    void remove(Frukt frukt);
    Frukt merge(Frukt frukt);
    void refresh(Frukt frukt);

    List<Frukt> listFrukt();
}
