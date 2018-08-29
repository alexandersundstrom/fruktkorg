package com.evry.fruktkorgpersistence.dao;

import com.evry.fruktkorgpersistence.model.Frukt;

import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public interface FruktDAO {
    void setEntityManagerFactory(EntityManagerFactory entityManagerFactory);

    void persist(Frukt frukt);
    void remove(long fruktId);
    Frukt merge(Frukt frukt);
    void refresh(Frukt frukt);
    Optional<Frukt> findFruktById(long fruktId);

    List<Frukt> listFrukt();
    List<String> listUniqueFruktTypes();
}
