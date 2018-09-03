package com.evry.fruktkorgservice.domain.service;

import com.evry.fruktkorgpersistence.dao.FruktDAO;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class FruktService {
    private FruktDAO fruktDAO;
    private static final Logger logger = LogManager.getLogger(FruktService.class);

    public FruktService(FruktDAO fruktDAO) {
        this.fruktDAO = fruktDAO;
    }

    public List<String> getUniqueFruktTypes() {
        logger.debug("getting unique Frukt types");
        return fruktDAO.findAllUniqueFruktTypes();
    }
}
