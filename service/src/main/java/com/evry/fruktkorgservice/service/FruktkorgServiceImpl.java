package com.evry.fruktkorgservice.service;

import com.evry.fruktkorgpersistence.dao.FruktkorgDAO;
import com.evry.fruktkorgpersistence.model.Frukt;
import com.evry.fruktkorgpersistence.model.Fruktkorg;
import com.evry.fruktkorgservice.exception.FruktMissingException;
import com.evry.fruktkorgservice.exception.FruktkorgMissingException;
import com.evry.fruktkorgservice.model.ImmutableFrukt;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.utils.ModelUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FruktkorgServiceImpl implements FruktkorgService {
    private FruktkorgDAO fruktkorgDAO;
    private static final Logger logger = LogManager.getLogger(FruktkorgServiceImpl.class);

    public FruktkorgServiceImpl(FruktkorgDAO fruktkorgDAO) {
        this.fruktkorgDAO = fruktkorgDAO;
    }

    @Override
    public ImmutableFruktkorg createFruktkorg(ImmutableFruktkorg immutableFruktkorg) {
        // TODO add null id validation
        logger.debug("Got request to create a Fruktkorg: " + immutableFruktkorg);
        Fruktkorg fruktkorg = ModelUtils.convertImmutableFruktkorg(immutableFruktkorg);

        fruktkorgDAO.persist(fruktkorg);

        return ModelUtils.convertFruktkorg(fruktkorg);
    }

    @Override
    public void deleteFruktkorg(long fruktkorgId) throws FruktkorgMissingException, IllegalArgumentException {
        logger.info("Got request to delete a fruktkorg with id " + fruktkorgId);
        fruktkorgDAO.remove(findFruktkorgById(fruktkorgId));
    }

    @Override
    public ImmutableFruktkorg addFruktToFruktkorg(long fruktkorgId, ImmutableFrukt immutableFrukt) throws FruktkorgMissingException {
        logger.debug("Got request to add Frukt to Fruktorg with id " + fruktkorgId + ": " + immutableFrukt);
        Optional<Fruktkorg> optFruktkorg = fruktkorgDAO.findFruktkorgById(fruktkorgId);

        if (!optFruktkorg.isPresent()) {
            logger.warn("Fruktkorg with id: " + fruktkorgId + " not found");
            throw new FruktkorgMissingException("Fruktkorg with id: " + fruktkorgId + " not found", fruktkorgId);
        }

        Fruktkorg fruktkorg = optFruktkorg.get();

        Frukt fruktToAdd = ModelUtils.convertImmutableFrukt(immutableFrukt);
        fruktToAdd.setFruktkorg(fruktkorg);

        boolean foundFrukt = false;
        for (Frukt frukt : fruktkorg.getFruktList()) {
            if (frukt.getType().equals(fruktToAdd.getType())) {
                frukt.setAmount(frukt.getAmount() + fruktToAdd.getAmount());
                foundFrukt = true;
                break;
            }
        }

        if (!foundFrukt) {
            fruktkorg.getFruktList().add(fruktToAdd);
        }

        fruktkorg = fruktkorgDAO.merge(fruktkorg);

        return ModelUtils.convertFruktkorg(fruktkorg);
    }

    @Override
    public ImmutableFruktkorg removeFruktFromFruktkorg(long fruktkorgId, String fruktType, int amount) throws FruktkorgMissingException, FruktMissingException {
        logger.info("Got request to remove " + amount + " Frukt(er) of type " + fruktType + " from Fruktkorg");
        Optional<Fruktkorg> optFruktkorg = fruktkorgDAO.findFruktkorgById(fruktkorgId);

        if (!optFruktkorg.isPresent()) {
            logger.warn("Fruktkorg with id: " + fruktkorgId + " not found");
            throw new FruktkorgMissingException("Fruktkorg with id: " + fruktkorgId + " not found", fruktkorgId);
        }

        Fruktkorg fruktkorg = optFruktkorg.get();

        boolean foundFrukt = false;
        for (Frukt frukt : fruktkorg.getFruktList()) {
            if (!frukt.getType().equals(fruktType)) {
                continue;
            }

            if (frukt.getAmount() > amount) {
                frukt.setAmount(frukt.getAmount() - amount);
            } else {
                fruktkorg.getFruktList().remove(frukt);
            }

            foundFrukt = true;
            break;
        }

        if (!foundFrukt) {
            logger.warn("Frukt with type: " + fruktType + " could not be found in fruktkorg with id: " + fruktkorgId);
            throw new FruktMissingException("Frukt with type: " + fruktType + " could not be found in fruktkorg with id: " + fruktkorgId, fruktType);
        }

        fruktkorg = fruktkorgDAO.merge(fruktkorg);

        return ModelUtils.convertFruktkorg(fruktkorg);
    }

    @Override
    public ImmutableFruktkorg getFruktkorgById(long fruktkorgId) throws IllegalArgumentException, FruktkorgMissingException {
        logger.debug("Got request to get Fruktkorg by id " + fruktkorgId);
        return ModelUtils.convertFruktkorg(findFruktkorgById(fruktkorgId));
    }

    @Override
    public List<ImmutableFruktkorg> searchFruktkorgByFrukt(String fruktType) {
        logger.debug("Searching for Fruktkorgar containing Frukt of type " + fruktType);
        List<Fruktkorg> fruktkorgList = fruktkorgDAO.findFruktkorgByFrukt(fruktType);

        List<ImmutableFruktkorg> immutableFruktkorgList = new ArrayList<>();

        for (Fruktkorg fruktkorg : fruktkorgList) {
            immutableFruktkorgList.add(ModelUtils.convertFruktkorg(fruktkorg));
        }

        return immutableFruktkorgList;
    }

    private Fruktkorg findFruktkorgById(long fruktkorgId) throws IllegalArgumentException, FruktkorgMissingException {
        validateId(fruktkorgId);

        Optional<Fruktkorg> optFruktkorg = fruktkorgDAO.findFruktkorgById(fruktkorgId);

        return optFruktkorg
                .orElseThrow(() ->  {
                    logger.warn("Unable to find fruktkorg with id: " + fruktkorgId);
                    return new FruktkorgMissingException("Unable to find fruktkorg with id: " + fruktkorgId, fruktkorgId);
                });
    }

    private void validateId(long fruktkorgId) throws IllegalArgumentException {
        if (fruktkorgId <= 0) {
            logger.warn("Invalid id: " + fruktkorgId + ", id cannot be lesser than 1");
            throw new IllegalArgumentException("Invalid id: " + fruktkorgId + ", id cannot be lesser than 1");
        }
    }
}
