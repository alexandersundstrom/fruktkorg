package com.evry.fruktkorgpersistence.hibernate;

import com.evry.fruktkorgpersistence.model.Report;
import com.evry.fruktkorgpersistence.model.ReportRepository;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceUnit;
import java.util.List;
import java.util.Optional;

public class ReportRepositoryHibernate implements ReportRepository {
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    private static final Logger logger = LogManager.getLogger(FruktkorgRepositoryHibernate.class);


    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    private EntityManager getEntityManager() {
        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
        }

        return entityManager;
    }

    @Override
    public void persist(Report report) {
        getEntityManager().getTransaction().begin();
        getEntityManager().persist(report);
        getEntityManager().getTransaction().commit();
    }

    @Override
    public Report merge(Report report) {
        getEntityManager().getTransaction().begin();
        Report mergedReport = getEntityManager().merge(report);
        getEntityManager().getTransaction().commit();
        return mergedReport;
    }

    @Override
    public void remove(Report report) {
        getEntityManager().getTransaction().begin();
        getEntityManager().remove(report);
        getEntityManager().getTransaction().commit();
    }

    @Override
    public List<Report> findAll() {
        return getEntityManager()
                .createNativeQuery("SELECT * FROM report", Report.class)
                .getResultList();
    }

    @Override
    public List<Report> findAllByLimitAndOffset(int limit, int offset) {
        return getEntityManager()
                .createNativeQuery("SELECT * FROM report", Report.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    @Override
    public Optional<Report> findById(long id) {
        try {
            return Optional.ofNullable((Report) getEntityManager()
                    .createNativeQuery("SELECT * FROM report WHERE report_id = ?1", Report.class)
                    .setParameter(1, id)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void removeByRead() {
        getEntityManager().getTransaction().begin();
        getEntityManager()
                .createNativeQuery("DELETE FROM report WHERE read")
                .executeUpdate();
        getEntityManager().getTransaction().commit();
    }

    @Override
    public List<Report> getAllByRead() {
        return getEntityManager()
                .createNativeQuery("SELECT * FROM report WHERE read", Report.class)
                .getResultList();
    }
}
