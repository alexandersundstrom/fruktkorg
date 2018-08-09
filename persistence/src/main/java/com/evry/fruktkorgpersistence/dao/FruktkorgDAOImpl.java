package com.evry.fruktkorgpersistence.dao;


import com.evry.fruktkorgpersistence.model.Fruktkorg;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class FruktkorgDAOImpl implements FruktkorgDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void persist(Fruktkorg fruktkorg) {
        entityManager.getTransaction().begin();
        entityManager.persist(fruktkorg);
        entityManager.getTransaction().commit();
    }

    @Override
    public void remove(long fruktkorgId) {
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("DELETE FROM fruktkorg WHERE fruktkorg_id = :fruktkorgId")
                .setParameter("fruktkorgId", fruktkorgId)
                .executeUpdate();
        entityManager.getTransaction().commit();
    }

    @Override
    public Fruktkorg merge(Fruktkorg fruktkorg) {
        entityManager.getTransaction().begin();
        Fruktkorg mergedFruktkorg = entityManager.merge(fruktkorg);
        entityManager.getTransaction().commit();
        return mergedFruktkorg;
    }

    @Override
    public void refresh(Fruktkorg fruktkorg) {
        entityManager.refresh(fruktkorg);
    }

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
