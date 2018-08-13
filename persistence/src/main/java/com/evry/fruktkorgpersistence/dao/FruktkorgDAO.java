package com.evry.fruktkorgpersistence.dao;


import com.evry.fruktkorgpersistence.model.Fruktkorg;

import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public interface FruktkorgDAO {
    void persist(Fruktkorg fruktkorg);
    void remove(long fruktkorgId);
    void remove(Fruktkorg fruktkorg);
    Fruktkorg merge(Fruktkorg fruktkorg);
    void refresh(Fruktkorg fruktkorg);

    List<Fruktkorg> listFruktkorg();
    List<Fruktkorg> findFruktkorgByFrukt(String fruktType);

    Optional<Fruktkorg> findFruktkorgById(long fruktkorgId);

    void setEntityManagerFactory(EntityManagerFactory entityManagerFactory);
}
