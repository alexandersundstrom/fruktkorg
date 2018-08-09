package com.evry.fruktkorgpersistence.dao;

import com.evry.fruktkorgpersistence.model.Frukt;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class FruktDAOImpl implements FruktDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void persist(Frukt frukt) {
        entityManager.getTransaction().begin();
        entityManager.persist(frukt);
        entityManager.getTransaction().commit();
    }

    @Override
    public void remove(long fruktId) {
        entityManager.getTransaction().begin();
        entityManager.remove(fruktId);
        entityManager.getTransaction().commit();
    }

    @Override
    public Frukt merge(Frukt frukt) {
        entityManager.getTransaction().begin();
        Frukt mergedFrukt = entityManager.merge(frukt);
        entityManager.getTransaction().commit();
        return mergedFrukt;
    }

    @Override
    public void refresh(Frukt frukt) {
        entityManager.refresh(frukt);
    }

    @Override
    public List<Frukt> listFrukt() {
//        return entityManager.createNativeQuery("SELECT * FROM frukt", Frukt.class).getResultList();
        CriteriaQuery<Frukt> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(Frukt.class);
        criteriaQuery.from(Frukt.class);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

}
