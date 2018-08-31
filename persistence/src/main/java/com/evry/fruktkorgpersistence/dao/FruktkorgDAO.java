package com.evry.fruktkorgpersistence.dao;


import com.evry.fruktkorgpersistence.model.Fruktkorg;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaQuery;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class FruktkorgDAO {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    private static final Logger logger = LogManager.getLogger(FruktkorgDAO.class);

    private EntityManager getEntityManager() {
        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
        }

        return entityManager;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void persist(Fruktkorg fruktkorg) {
        logger.debug("Persisting Fruktkorg: " + fruktkorg);
        if (!fruktkorg.getFruktList().isEmpty()) {
            fruktkorg.setLastChanged(Instant.now());
        }
        EntityManager entityManager = getEntityManager();

        entityManager.getTransaction().begin();
        entityManager.persist(fruktkorg);
        entityManager.getTransaction().commit();
    }

    public void remove(long fruktkorgId) {
        logger.info("Removing Fruktkorg with id: " + fruktkorgId);
        EntityManager entityManager = getEntityManager();

        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("DELETE FROM fruktkorg WHERE fruktkorg_id = :fruktkorgId")
                .setParameter("fruktkorgId", fruktkorgId)
                .executeUpdate();
        entityManager.getTransaction().commit();
    }

    public void remove(Fruktkorg fruktkorg) {
        getEntityManager().detach(fruktkorg);
        remove(fruktkorg.getId());
    }

    public void removeAllBefore(Instant before) {
        logger.info("Removing Fruktkorgar with date before " + before.toString());
        EntityManager entityManager = getEntityManager();

        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("DELETE FROM fruktkorg WHERE last_changed < :before")
                .setParameter("before", before)
                .executeUpdate();
        entityManager.getTransaction().commit();
    }

    public Fruktkorg merge(Fruktkorg fruktkorg) {
        logger.debug("Merging Fruktkorg: " + fruktkorg);
        EntityManager entityManager = getEntityManager();

        entityManager.getTransaction().begin();
        Fruktkorg mergedFruktkorg = entityManager.merge(fruktkorg);
        entityManager.getTransaction().commit();
        return mergedFruktkorg;
    }

    public void refresh(Fruktkorg fruktkorg) {
        getEntityManager().refresh(fruktkorg);
    }

    public List<Fruktkorg> listFruktkorgar() {
        logger.debug("Fetching all Fruktkorgar");
        EntityManager entityManager = getEntityManager();

        CriteriaQuery<Fruktkorg> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(Fruktkorg.class);
        criteriaQuery.from(Fruktkorg.class);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<Fruktkorg> findFruktkorgByFrukt(String fruktType) {
        return getEntityManager()
                .createNativeQuery("SELECT fk.* FROM fruktkorg fk JOIN frukt f ON(fk.fruktkorg_id = f.fruktkorg_id) WHERE f.type = ?1", Fruktkorg.class)
                .setParameter(1, fruktType)
                .getResultList();
    }

    public Optional<Fruktkorg> findFruktkorgById(long fruktkorgId) {
        return Optional.ofNullable(getEntityManager().find(Fruktkorg.class, fruktkorgId));
    }
}
