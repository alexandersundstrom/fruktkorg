package com.evry.fruktkorgservice.domain.service;

import com.evry.fruktkorgpersistence.hibernate.FruktRepositoryHibernate;
import com.evry.fruktkorgpersistence.hibernate.FruktkorgRepositoryHibernate;
import com.evry.fruktkorgpersistence.model.Frukt;
import com.evry.fruktkorgpersistence.model.Fruktkorg;
import com.evry.fruktkorgservice.domain.model.ImmutableFrukt;
import com.evry.fruktkorgservice.domain.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.exception.FruktMissingException;
import com.evry.fruktkorgservice.exception.FruktkorgMissingException;
import com.evry.fruktkorgservice.utils.ModelUtils;
import com.evry.fruktkorgservice.utils.XMLUtils;
import com.evry.fruktkorgservice.xml.FruktkorgRestore;
import com.evry.fruktkorgservice.xml.FruktkorgUpdate;
import com.evry.fruktkorgservice.xml.FruktkorgarRestore;
import com.evry.fruktkorgservice.xml.FruktkorgarUpdate;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FruktkorgService {
    private FruktkorgRepositoryHibernate fruktkorgRepository;
    private FruktRepositoryHibernate fruktRepository;
    private static final Logger logger = LogManager.getLogger(FruktkorgService.class);


    public FruktkorgService(FruktkorgRepositoryHibernate fruktkorgRepositoryHibernate, FruktRepositoryHibernate fruktRepositoryHibernate) {
        this.fruktkorgRepository = fruktkorgRepositoryHibernate;
        this.fruktRepository = fruktRepositoryHibernate;
    }

    public ImmutableFruktkorg createFruktkorg(ImmutableFruktkorg immutableFruktkorg) {
        // TODO add null id validation
        logger.debug("Got request to create a Fruktkorg: " + immutableFruktkorg);
        Fruktkorg fruktkorg = ModelUtils.convertImmutableFruktkorg(immutableFruktkorg);

        fruktkorgRepository.persist(fruktkorg);

        return ModelUtils.convertFruktkorg(fruktkorg);
    }

    public void deleteFruktkorg(long fruktkorgId) throws FruktkorgMissingException, IllegalArgumentException {
        logger.info("Got request to delete a fruktkorg with id " + fruktkorgId);
        fruktkorgRepository.remove(findFruktkorgById(fruktkorgId));
    }

    public ImmutableFruktkorg addFruktToFruktkorg(long fruktkorgId, ImmutableFrukt immutableFrukt) throws FruktkorgMissingException {
        logger.debug("Got request to add Frukt to Fruktorg with id " + fruktkorgId + ": " + immutableFrukt);
        Optional<Fruktkorg> optFruktkorg = fruktkorgRepository.findById(fruktkorgId);

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

        fruktkorg.setLastChanged(Instant.now());
        fruktkorg = fruktkorgRepository.merge(fruktkorg);

        return ModelUtils.convertFruktkorg(fruktkorg);
    }

    public ImmutableFruktkorg removeFruktFromFruktkorg(long fruktkorgId, String fruktType, int amount) throws FruktkorgMissingException, FruktMissingException {
        logger.info("Got request to remove " + amount + " Frukt(er) of type " + fruktType + " from Fruktkorg");
        Optional<Fruktkorg> optFruktkorg = fruktkorgRepository.findById(fruktkorgId);

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

        fruktkorg = fruktkorgRepository.merge(fruktkorg);

        return ModelUtils.convertFruktkorg(fruktkorg);
    }

    public ImmutableFruktkorg getFruktkorgById(long fruktkorgId) throws IllegalArgumentException, FruktkorgMissingException {
        logger.debug("Got request to get Fruktkorg by id " + fruktkorgId);
        return ModelUtils.convertFruktkorg(findFruktkorgById(fruktkorgId));
    }

    public List<ImmutableFruktkorg> searchFruktkorgByFrukt(String fruktType) {
        logger.debug("Searching for Fruktkorgar containing Frukt of type " + fruktType);
        List<Fruktkorg> fruktkorgList = fruktkorgRepository.findAllByFruktType(fruktType);

        List<ImmutableFruktkorg> immutableFruktkorgList = new ArrayList<>();

        for (Fruktkorg fruktkorg : fruktkorgList) {
            immutableFruktkorgList.add(ModelUtils.convertFruktkorg(fruktkorg));
        }

        return immutableFruktkorgList;
    }

    public List<ImmutableFruktkorg> listFruktkorgar() {
        return fruktkorgRepository.findAll().stream().map(ModelUtils::convertFruktkorg).collect(Collectors.toList());
    }

    private ImmutableFruktkorg updateFruktkorg(FruktkorgUpdate fruktkorgUpdate) throws FruktkorgMissingException {
        Optional<Fruktkorg> optFruktkorg = fruktkorgRepository.findById(fruktkorgUpdate.id);

        if (!optFruktkorg.isPresent()) {
            throw new FruktkorgMissingException("Unable to find fruktkorg with id: " + fruktkorgUpdate.id, fruktkorgUpdate.id);
        }

        Fruktkorg fruktkorg = optFruktkorg.get();
        fruktkorg.getFruktList().clear();

        fruktkorg = fruktkorgRepository.merge(fruktkorg);

        for (ImmutableFrukt immutableFrukt : fruktkorgUpdate.fruktList) {
            Frukt frukt = new Frukt();
            frukt.setType(immutableFrukt.getType());
            frukt.setAmount(immutableFrukt.getAmount());
            frukt.setFruktkorg(fruktkorg);

            fruktkorg.getFruktList().add(frukt);
        }

        fruktkorg.setLastChanged(Instant.now());
        fruktkorg = fruktkorgRepository.merge(fruktkorg);

        return ModelUtils.convertFruktkorg(fruktkorg);
    }

    public List<ImmutableFruktkorg> updateFruktkorgar(InputStream inputStream) throws FruktkorgMissingException, JAXBException {

        Unmarshaller unmarshaller = XMLUtils.getUnmarshaller(XMLUtils.UPDATE_XSD);

        FruktkorgarUpdate fruktkorgarUpdate;
        try {
            fruktkorgarUpdate = (FruktkorgarUpdate) unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            logger.error("Error unmarshaling", e);
            throw e;
        }
        validateFruktkorgar(fruktkorgarUpdate);
        List<ImmutableFruktkorg> updatedFruktkorgar = new ArrayList<>();
        for (FruktkorgUpdate fruktkorgUpdate : fruktkorgarUpdate.fruktkorgList) {
            try {
                updatedFruktkorgar.add(updateFruktkorg(fruktkorgUpdate));
            } catch (FruktkorgMissingException e) {
                logger.warn("Caught the following exception", e);
                throw e;
            }
        }

        return updatedFruktkorgar;
    }

    private ImmutableFruktkorg restoreFruktkorg(FruktkorgRestore fruktkorgRestore) throws FruktkorgMissingException, FruktMissingException {
        Fruktkorg fruktkorg;
        if (fruktkorgRestore.id == 0L) {
            fruktkorg = new Fruktkorg();
            fruktkorg.setName(fruktkorgRestore.name);
            fruktkorg.setLastChanged(Instant.now());
            fruktkorgRepository.persist(fruktkorg);

        } else {
            Optional<Fruktkorg> optFruktkorg = fruktkorgRepository.findById(fruktkorgRestore.id);

            if (!optFruktkorg.isPresent()) {
                throw new FruktkorgMissingException("Kunde inte hitta fruktkorg med id " + fruktkorgRestore.id, fruktkorgRestore.id);
            }
            fruktkorg = optFruktkorg.get();
            fruktkorg.setName(fruktkorgRestore.name);
            fruktkorg.getFruktList().clear();
        }

        for (ImmutableFrukt immutableFrukt : fruktkorgRestore.fruktList) {
            if (immutableFrukt.getId() != 0L) {
                Optional<Frukt> optFrukt = fruktRepository.findById(immutableFrukt.getId());
                if (!optFrukt.isPresent()) {
                    throw new FruktMissingException("Kunde inte hitta frukt med id " + immutableFrukt.getId(), immutableFrukt.getType());
                }
            }

            Frukt frukt = new Frukt();
            frukt.setType(immutableFrukt.getType());
            frukt.setAmount(immutableFrukt.getAmount());
            frukt.setId(immutableFrukt.getId());
            frukt.setFruktkorg(fruktkorg);

            fruktkorg.getFruktList().add(frukt);
        }

        fruktkorg.setLastChanged(Instant.now());
        fruktkorg = fruktkorgRepository.merge(fruktkorg);

        return ModelUtils.convertFruktkorg(fruktkorg);
    }

    public List<ImmutableFruktkorg> restoreFruktkorgar(InputStream inputStream) throws FruktkorgMissingException, FruktMissingException, JAXBException {
        Unmarshaller unmarshaller = XMLUtils.getUnmarshaller(XMLUtils.RESTORE_XSD);

        FruktkorgarRestore fruktkorgarRestore;
        try {
            fruktkorgarRestore = (FruktkorgarRestore) unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            logger.error("Error unmarshaling", e);
            throw e;
        }

        validateFruktkorgar(fruktkorgarRestore);
        Instant restorationPoint = Instant.now();
        List<ImmutableFruktkorg> restoredFruktkorgar = new ArrayList<>();
        for (FruktkorgRestore fruktkorg : fruktkorgarRestore.fruktkorgList) {
            restoredFruktkorgar.add(restoreFruktkorg(fruktkorg));
        }

        fruktkorgRepository.removeAllBefore(restorationPoint);
        return restoredFruktkorgar;
    }

    public void validateFruktkorgar(FruktkorgarUpdate fruktkorgarUpdate) throws FruktkorgMissingException {
        for (FruktkorgUpdate fruktkorg : fruktkorgarUpdate.fruktkorgList) {
            if (fruktkorg.id != 0L) {
                validateFruktkorg(fruktkorg.id);
            } else {
                throw new FruktkorgMissingException("Fruktkorgs id måste vara satt.", fruktkorg.id);
            }
        }
    }

    public void validateFruktkorgar(FruktkorgarRestore fruktkorgarRestore) throws FruktkorgMissingException, FruktMissingException {
        for (FruktkorgRestore fruktkorg : fruktkorgarRestore.fruktkorgList) {
            if (fruktkorg.id != 0L) {
                validateFruktkorg(fruktkorg.id);
            }

            for (ImmutableFrukt frukt : fruktkorg.fruktList) {
                if (frukt.getId() != 0L) {
                    validateFrukt(frukt);
                }
            }
        }
    }

    private void validateFruktkorg(long id) throws FruktkorgMissingException {
        Optional<Fruktkorg> optFruktkorg = fruktkorgRepository.findById(id);

        if (!optFruktkorg.isPresent()) {
            throw new FruktkorgMissingException("Kunde inte hitta fruktkorg med id " + id, id);
        }
    }

    public void validateFrukt(ImmutableFrukt frukt) throws FruktMissingException {
        Optional<Frukt> optFrukt = fruktRepository.findById(frukt.getId());

        if (!optFrukt.isPresent()) {
            throw new FruktMissingException("Kunde inte hitta frukt med id " + frukt.getId(), frukt.getType());
        }
    }

    private Fruktkorg findFruktkorgById(long fruktkorgId) throws IllegalArgumentException, FruktkorgMissingException {
        validateId(fruktkorgId);

        Optional<Fruktkorg> optFruktkorg = fruktkorgRepository.findById(fruktkorgId);

        return optFruktkorg
                .orElseThrow(() -> {
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
