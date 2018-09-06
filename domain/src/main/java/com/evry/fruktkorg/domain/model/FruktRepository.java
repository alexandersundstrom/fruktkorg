package com.evry.fruktkorg.domain.model;

import java.util.List;
import java.util.Optional;

public interface FruktRepository {

    Optional<Frukt> findById(long fruktId);

    List<Frukt> findAll();

    List<String> findAllUniqueFruktTypes();
}
