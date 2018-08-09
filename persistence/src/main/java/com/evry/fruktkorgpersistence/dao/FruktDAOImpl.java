package com.evry.fruktkorgpersistence.dao;

import com.evry.fruktkorgpersistence.model.Frukt;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class FruktDAOImpl implements FruktDAO {
    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger logger = LogManager.getLogger(FruktDAOImpl.class);

    @Override
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void persist(Frukt frukt) {
        logger.debug("Persisting Frukt: " + frukt);
        entityManager.getTransaction().begin();
        entityManager.persist(frukt);
        entityManager.getTransaction().commit();
    }

    @Override
    public void remove(Frukt frukt) {
        logger.info("Removing frukt: " + frukt);
        entityManager.getTransaction().begin();
        entityManager.remove(frukt);
        entityManager.getTransaction().commit();
    }

    @Override
    public Frukt merge(Frukt frukt) {
        logger.debug("Merging Frukt: " + frukt);
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
        logger.debug("Fetching all Frukt");
        CriteriaQuery<Frukt> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(Frukt.class);
        criteriaQuery.from(Frukt.class);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

}
