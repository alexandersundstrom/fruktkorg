package com.evry.fruktkorgservice.service;

import com.evry.fruktkorgpersistence.dao.FruktkorgDAO;
import com.evry.fruktkorgpersistence.model.Fruktkorg;

import java.util.List;

public class FruktkorgServiceImpl implements FruktkorgService {
    private FruktkorgDAO fruktkorgDAO;

    public FruktkorgServiceImpl(FruktkorgDAO fruktkorgDAO) {
        this.fruktkorgDAO = fruktkorgDAO;
    }

    @Override
    public List<Fruktkorg> listFruktkorg() {
        return fruktkorgDAO.listFruktkorg();
    }

    @Override
    public List<Fruktkorg> findFruktkorgByFrukt() {
        return fruktkorgDAO.findFruktkorgByFrukt();
    }

    @Override
    public void persist(Fruktkorg fruktkorg) {
        fruktkorgDAO.persist(fruktkorg);
    }

    @Override
    public void remove(Fruktkorg fruktkorg) {
        remove(fruktkorg.getId());
    }

    @Override
    public void remove(long fruktkorgId) {
        fruktkorgDAO.remove(fruktkorgId);
    }

    @Override
    public Fruktkorg merge(Fruktkorg fruktkorg) {
        return fruktkorgDAO.merge(fruktkorg);
    }

    @Override
    public void refresh(Fruktkorg fruktkorg) {
        fruktkorgDAO.refresh(fruktkorg);
    }
}
