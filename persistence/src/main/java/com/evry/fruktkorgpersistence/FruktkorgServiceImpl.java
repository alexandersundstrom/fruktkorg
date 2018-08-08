package com.evry.fruktkorgpersistence;

import com.evry.fruktkorgpersistence.model.Fruktkorg;

import javax.transaction.Transactional;
import java.util.List;

public class FruktkorgServiceImpl implements FruktkorgService {
    private FruktkorgDAO fruktkorgDAO;

    public FruktkorgServiceImpl(FruktkorgDAO fruktkorgDAO) {
        this.fruktkorgDAO = fruktkorgDAO;
    }

    @Override
    @Transactional
    public List<Fruktkorg> listFruktkorg() {
        return fruktkorgDAO.listFruktkorg();
    }

    @Override
    @Transactional
    public List<Fruktkorg> findFruktkorgByFrukt() {
        return fruktkorgDAO.findFruktkorgByFrukt();
    }
}
