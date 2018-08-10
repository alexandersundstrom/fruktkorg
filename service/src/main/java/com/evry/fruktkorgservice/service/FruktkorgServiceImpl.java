package com.evry.fruktkorgservice.service;

import com.evry.fruktkorgpersistence.dao.FruktkorgDAO;
import com.evry.fruktkorgpersistence.model.Frukt;
import com.evry.fruktkorgpersistence.model.Fruktkorg;
import com.evry.fruktkorgservice.exception.FruktMissingException;
import com.evry.fruktkorgservice.exception.FruktkorgMissingException;
import com.evry.fruktkorgservice.model.ImmutableFrukt;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.utils.ModelUtils;

import java.util.Optional;

public class FruktkorgServiceImpl implements FruktkorgService {
    private FruktkorgDAO fruktkorgDAO;

    public FruktkorgServiceImpl(FruktkorgDAO fruktkorgDAO) {
        this.fruktkorgDAO = fruktkorgDAO;
    }

    @Override
    public ImmutableFruktkorg createFruktkorg(ImmutableFruktkorg immutableFruktkorg) {
        // TODO add null id validation

        Fruktkorg fruktkorg = ModelUtils.convertImmutableFruktkorg(immutableFruktkorg);

        fruktkorgDAO.persist(fruktkorg);

        return ModelUtils.convertFruktkorg(fruktkorg);
    }

    @Override
    public void deleteFruktkorg(long fruktkorgId) throws FruktkorgMissingException, IllegalArgumentException {
        fruktkorgDAO.remove(getFruktkorgById(fruktkorgId).getId());
    }

    @Override
    public ImmutableFruktkorg addFruktToFruktkorg(long fruktkorgId, ImmutableFrukt immutableFrukt) throws FruktkorgMissingException {
        Optional<Fruktkorg> optFruktkorg = fruktkorgDAO.findFruktkorgById(fruktkorgId);

        if(!optFruktkorg.isPresent()) {
            throw new FruktkorgMissingException("Fruktkorg with id: " + fruktkorgId + " not found", fruktkorgId);
        }

        Fruktkorg fruktkorg = optFruktkorg.get();

        Frukt fruktToAdd = ModelUtils.convertImmutableFrukt(immutableFrukt);
        fruktToAdd.setFruktkorg(fruktkorg);

        boolean foundFrukt = false;
        for(Frukt frukt : fruktkorg.getFruktList()) {
            if(frukt.getType().equals(fruktToAdd.getType())) {
                frukt.setAmount(frukt.getAmount() + fruktToAdd.getAmount());
                foundFrukt = true;
                break;
            }
        }

        if(!foundFrukt) {
            fruktkorg.getFruktList().add(fruktToAdd);
        }

        fruktkorg = fruktkorgDAO.merge(fruktkorg);

        return ModelUtils.convertFruktkorg(fruktkorg);
    }

    @Override
    public ImmutableFruktkorg removeFruktFromFruktkorg(long fruktkorgId, String fruktType, int amount) throws FruktkorgMissingException, FruktMissingException {
        Optional<Fruktkorg> optFruktkorg = fruktkorgDAO.findFruktkorgById(fruktkorgId);

        if(!optFruktkorg.isPresent()) {
            throw new FruktkorgMissingException("Fruktkorg with id: " + fruktkorgId + " not found", fruktkorgId);
        }

        Fruktkorg fruktkorg = optFruktkorg.get();

        boolean foundFrukt = false;
        for(Frukt frukt : fruktkorg.getFruktList()) {
            if(!frukt.getType().equals(fruktType)) {
                continue;
            }

            if(frukt.getAmount() > amount) {
                frukt.setAmount(frukt.getAmount() - amount);
            } else {
                fruktkorg.getFruktList().remove(frukt);
            }

            foundFrukt = true;
            break;
        }

        if(!foundFrukt) {
            throw new FruktMissingException("Frukt with type: " + fruktType + " could not be found in fruktkorg with id: " + fruktkorgId, fruktType);
        }

        fruktkorg = fruktkorgDAO.merge(fruktkorg);

        return ModelUtils.convertFruktkorg(fruktkorg);
    }

    @Override
    public ImmutableFruktkorg getFruktkorgById(long fruktkorgId) throws IllegalArgumentException, FruktkorgMissingException {
        validateId(fruktkorgId);

        Optional<Fruktkorg> optFruktkorg = fruktkorgDAO.findFruktkorgById(fruktkorgId);

        return optFruktkorg
                .map(ModelUtils::convertFruktkorg)
                .orElseThrow(() -> new FruktkorgMissingException("Unable to find fruktkorg with id: " + fruktkorgId, fruktkorgId));
    }

    private void validateId(long fruktkorgId) throws IllegalArgumentException {
        if(fruktkorgId <= 0) {
            throw new IllegalArgumentException("Invalid id: " + fruktkorgId + ", id cannot be lesser than 1");
        }
    }
}
