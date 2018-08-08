package com.evry.fruktkorg.persistence;

import com.evry.fruktkorg.model.Fruktkorg;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FruktkorgDAOImpl implements FruktkorgDAO {

    private SessionFactory sessionFactory;

    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Fruktkorg> listFruktkorg() {
        Session session = sessionFactory.getCurrentSession();
        List<Fruktkorg> fruktkorgList = session.createSQLQuery("SELECT * FROM fruktkorg").list();
        return fruktkorgList;
    }
}
