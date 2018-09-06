package com.evry.fruktkorg.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface FruktkorgRepository {

    void persist(Fruktkorg fruktkorg);

    void remove(long fruktkorgId);

    void remove(Fruktkorg fruktkorg);

    void removeAllBefore(Instant before);

    Fruktkorg merge(Fruktkorg fruktkorg);

    void refresh(Fruktkorg fruktkorg);

    List<Fruktkorg> findAll();

    List<Fruktkorg> findAllByFruktType(String fruktType);

    Optional<Fruktkorg> findById(long fruktkorgId);

}
