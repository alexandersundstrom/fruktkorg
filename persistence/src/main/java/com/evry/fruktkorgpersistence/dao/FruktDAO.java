package com.evry.fruktkorgpersistence.dao;

import com.evry.fruktkorgpersistence.model.Frukt;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Optional;

public class FruktDAO {
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    private static final Logger logger = LogManager.getLogger(FruktDAO.class);

    private EntityManager getEntityManager() {
        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
        }

        return entityManager;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void persist(Frukt frukt) {
        logger.debug("Persisting Frukt: " + frukt);
        EntityManager entityManager = getEntityManager();

        entityManager.getTransaction().begin();
        entityManager.persist(frukt);
        entityManager.getTransaction().commit();
    }

    public void remove(long fruktId) {
        logger.info("Removing frukt with id: " + fruktId);
        EntityManager entityManager = getEntityManager();

        entityManager.getTransaction().begin();
        entityManager.remove(fruktId);
        entityManager.getTransaction().commit();
    }

    public Frukt merge(Frukt frukt) {
        logger.debug("Merging Frukt: " + frukt);
        EntityManager entityManager = getEntityManager();

        entityManager.getTransaction().begin();
        Frukt mergedFrukt = entityManager.merge(frukt);
        entityManager.getTransaction().commit();
        return mergedFrukt;
    }

    public void refresh(Frukt frukt) {
        getEntityManager().refresh(frukt);
    }

    public Optional<Frukt> findFruktById(long fruktId) {
        return Optional.ofNullable(getEntityManager().find(Frukt.class, fruktId));
    }

    public List<Frukt> listFrukt() {
        logger.debug("Fetching all Frukt");
        EntityManager entityManager = getEntityManager();

        CriteriaQuery<Frukt> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(Frukt.class);
        criteriaQuery.from(Frukt.class);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<String> listUniqueFruktTypes() {
        logger.debug("Fetching all unique Frukt types");
        EntityManager entityManager = getEntityManager();

        return entityManager.createNativeQuery("SELECT DISTINCT type FROM frukt ORDER BY type ASC").getResultList();
    }

}
