package com.evry.fruktkorg.persistence;

import com.evry.fruktkorg.model.Fruktkorg;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

@Repository
public class FruktkorgDAOImpl implements FruktkorgDAO {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Fruktkorg> listFruktkorg() {
        CriteriaQuery<Fruktkorg> criteriaQuery = entityManager.getCriteriaBuilder().createQuery(Fruktkorg.class);
        criteriaQuery.from(Fruktkorg.class);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
