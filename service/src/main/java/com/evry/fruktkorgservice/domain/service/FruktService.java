package com.evry.fruktkorgservice.domain.service;

import com.evry.fruktkorgpersistence.dao.FruktRepositoryHibernate;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class FruktService {
    private FruktRepositoryHibernate fruktRepository;
    private static final Logger logger = LogManager.getLogger(FruktService.class);

    public FruktService(FruktRepositoryHibernate fruktRepositoryHibernate) {
        this.fruktRepository = fruktRepositoryHibernate;
    }

    public List<String> getUniqueFruktTypes() {
        logger.debug("getting unique Frukt types");
        return fruktRepository.findAllUniqueFruktTypes();
    }
}
