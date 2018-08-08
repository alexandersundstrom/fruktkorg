package com.evry.fruktkorg.persistence;

import com.evry.fruktkorg.model.Fruktkorg;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
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
}
