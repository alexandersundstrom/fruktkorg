package com.evry.fruktkorgpersistence.service;

import com.evry.fruktkorgpersistence.dao.FruktDAO;
import com.evry.fruktkorgpersistence.model.Frukt;

import java.util.List;

public class FruktServiceImpl implements FruktService {
    private FruktDAO fruktDAO;

    public FruktServiceImpl(FruktDAO fruktDAO) {
        this.fruktDAO = fruktDAO;
    }

    @Override
    public void persist(Frukt frukt) {
        fruktDAO.persist(frukt);
    }

    @Override
    public void remove(Frukt frukt) {
        fruktDAO.remove(frukt);
    }

    @Override
    public Frukt merge(Frukt frukt) {
        return fruktDAO.merge(frukt);
    }

    @Override
    public void refresh(Frukt frukt) {
        fruktDAO.refresh(frukt);
    }

    @Override
    public List<Frukt> listFrukt() {
        return fruktDAO.listFrukt();
    }
}
