package com.evry.fruktkorgpersistence.dao;


import com.evry.fruktkorgpersistence.model.Fruktkorg;

import javax.persistence.EntityManager;
import java.util.List;

public interface FruktkorgDAO {
    void persist(Fruktkorg fruktkorg);
    void remove(long fruktkorgId);
    Fruktkorg merge(Fruktkorg fruktkorg);
    void refresh(Fruktkorg fruktkorg);

    List<Fruktkorg> listFruktkorg();
    List<Fruktkorg> findFruktkorgByFrukt();
    void setEntityManager(EntityManager entityManager);
}
