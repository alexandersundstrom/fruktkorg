package com.evry.fruktkorgservice.service;

import com.evry.fruktkorgpersistence.dao.FruktDAO;

public class FruktServiceImpl implements FruktService {
    private FruktDAO fruktDAO;

    public FruktServiceImpl(FruktDAO fruktDAO) {
        this.fruktDAO = fruktDAO;
    }

    @Override
    public void deleteFrukt(long fruktId) {
        fruktDAO.remove(fruktId);
    }
}
