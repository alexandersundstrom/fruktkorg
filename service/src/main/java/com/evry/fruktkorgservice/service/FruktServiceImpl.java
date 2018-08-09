package com.evry.fruktkorgservice.service;

import com.evry.fruktkorgpersistence.dao.FruktDAO;
import com.evry.fruktkorgpersistence.model.Frukt;
import com.evry.fruktkorgservice.model.ImmutableFrukt;
import com.evry.fruktkorgservice.utils.ModelUtils;

public class FruktServiceImpl implements FruktService {
    private FruktDAO fruktDAO;

    public FruktServiceImpl(FruktDAO fruktDAO) {
        this.fruktDAO = fruktDAO;
    }

    @Override
    public void deleteFrukt(long fruktId) {

    }
}
