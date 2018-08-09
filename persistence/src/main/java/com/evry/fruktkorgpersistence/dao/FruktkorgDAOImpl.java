package com.evry.fruktkorgpersistence.dao;


import com.evry.fruktkorgpersistence.model.Fruktkorg;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Optional;

public class FruktkorgDAOImpl implements FruktkorgDAO {

    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger logger = LogManager.getLogger(FruktkorgDAOImpl.class);

    @Override
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void persist(Fruktkorg fruktkorg) {
        logger.debug("Persisting Fruktkorg: " + fruktkorg);
        entityManager.getTransaction().begin();
        entityManager.persist(fruktkorg);
        entityManager.getTransaction().commit();
    }

    @Override
    public void remove(long fruktkorgId) {
        logger.info("Removing Fruktkorg with id: " + fruktkorgId);
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("DELETE FROM fruktkorg WHERE fruktkorg_id = :fruktkorgId")
                .setParameter("fruktkorgId", fruktkorgId)
                .executeUpdate();
        entityManager.getTransaction().commit();
    }

    @Override
    public Fruktkorg merge(Fruktkorg fruktkorg) {
        logger.debug("Merging Fruktkorg: " + fruktkorg);
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
        logger.debug("Fetching all Fruktkorgar");
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

    @Override
    public Optional<Fruktkorg> findFruktkorgById(long fruktkorgId) {
        return Optional.ofNullable(entityManager.find(Fruktkorg.class, fruktkorgId));
    }
}
