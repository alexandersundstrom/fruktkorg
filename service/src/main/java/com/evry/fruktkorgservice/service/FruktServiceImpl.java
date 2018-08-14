package com.evry.fruktkorgservice.service;

import com.evry.fruktkorgpersistence.dao.FruktDAO;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class FruktServiceImpl implements FruktService {
    private FruktDAO fruktDAO;
    private static final Logger logger = LogManager.getLogger(FruktServiceImpl.class);

    public FruktServiceImpl(FruktDAO fruktDAO) {
        this.fruktDAO = fruktDAO;
    }

    @Override
    public List<String> getUniqueFruktTypes() {
        logger.debug("getting unique Frukt types");
        return fruktDAO.listUniqueFruktTypes();
    }
}
