package com.evry.fruktkorgpersistence;


import com.evry.fruktkorgpersistence.model.Fruktkorg;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class FruktkorgDAOImpl implements FruktkorgDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Fruktkorg> listFruktkorg() {
        CriteriaQuery<Fruktkorg> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(Fruktkorg.class);
        criteriaQuery.from(Fruktkorg.class);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<Fruktkorg> findFruktkorgByFrukt() {
        return entityManager
                .createNativeQuery("SELECT fk.* FROM fruktkorg fk JOIN frukt f USING(fruktkorg_id) WHERE f.type = :fruktType", Fruktkorg.class)
                .setParameter("fruktType", "banan")
                .getResultList();
    }
}
