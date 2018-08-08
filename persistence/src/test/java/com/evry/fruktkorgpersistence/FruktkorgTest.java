package com.evry.fruktkorgpersistence;

import com.evry.fruktkorgpersistence.model.Fruktkorg;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class FruktkorgTest {

    private static Configuration config;
    private static SessionFactory factory;
    private static Session hibernateSession;
    private static EntityManager em;

    @BeforeAll
    public static void init() {
//        config = new AnnotationConfiguration();
//        config.configure(new File("hibernate-test.cfg.xml"));
//        factory = config.buildSessionFactory();
//        hibernateSession = factory.openSession();
        em = Persistence.createEntityManagerFactory("test").createEntityManager();
    }

    @Test
    public void saveFruktkorgAndRead() {
        Fruktkorg fruktkorg = new Fruktkorg();
        fruktkorg.setName("Test korg");
        em.persist(fruktkorg);

        CriteriaQuery<Fruktkorg> criteriaQuery = em.getCriteriaBuilder().createQuery(Fruktkorg.class);
        criteriaQuery.from(Fruktkorg.class);
        List<Fruktkorg> fruktkorgar = em.createQuery(criteriaQuery).getResultList();
        Assertions.assertEquals( 1, fruktkorgar.size(), "Should return 1 fruktkorg");
    }
}
