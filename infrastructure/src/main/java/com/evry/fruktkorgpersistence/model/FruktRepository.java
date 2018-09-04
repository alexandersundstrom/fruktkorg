package com.evry.fruktkorgpersistence.model;

import java.util.List;
import java.util.Optional;

public interface FruktRepository {
    void persist(Frukt frukt);

    Frukt merge(Frukt frukt);

    Optional<Frukt> findById(long fruktId);

    List<Frukt> findAll();

    List<String> findAllUniqueFruktTypes();
}
