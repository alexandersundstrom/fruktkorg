package com.evry.fruktkorgpersistence.hibernate;

import com.evry.fruktkorg.domain.model.Frukt;
import com.evry.fruktkorg.domain.model.FruktRepository;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Optional;

public class FruktRepositoryHibernate implements FruktRepository {
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    private static final Logger logger = LogManager.getLogger(FruktRepositoryHibernate.class);

    private EntityManager getEntityManager() {
        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
        }

        return entityManager;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }


    @Override
    public Optional<Frukt> findById(long fruktId) {
        return Optional.ofNullable(getEntityManager().find(Frukt.class, fruktId));
    }

    @Override
    public List<Frukt> findAll() {
        logger.debug("Fetching all Frukt");
        EntityManager entityManager = getEntityManager();

        CriteriaQuery<Frukt> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(Frukt.class);
        criteriaQuery.from(Frukt.class);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<String> findAllUniqueFruktTypes() {
        logger.debug("Fetching all unique Frukt types");
        EntityManager entityManager = getEntityManager();

        return entityManager.createNativeQuery("SELECT DISTINCT type FROM frukt ORDER BY type ASC").getResultList();
    }

}
