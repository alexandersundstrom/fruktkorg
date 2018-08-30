package com.evry.fruktkorgservice.service;


import com.evry.fruktkorgservice.exception.FruktMissingException;
import com.evry.fruktkorgservice.exception.FruktkorgMissingException;
import com.evry.fruktkorgservice.model.ImmutableFrukt;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;

import java.io.InputStream;
import java.util.List;

public interface FruktkorgService {
    ImmutableFruktkorg createFruktkorg(ImmutableFruktkorg immutableFruktkorg);
    void deleteFruktkorg(long fruktkorgId) throws FruktkorgMissingException, IllegalArgumentException;
    ImmutableFruktkorg addFruktToFruktkorg(long fruktkorgId, ImmutableFrukt immutableFrukt) throws FruktkorgMissingException;
    ImmutableFruktkorg removeFruktFromFruktkorg(long fruktkorgId, String fruktType, int amount) throws FruktkorgMissingException, FruktMissingException;
    ImmutableFruktkorg getFruktkorgById(long fruktkorgId) throws IllegalArgumentException, FruktkorgMissingException;
    List<ImmutableFruktkorg> searchFruktkorgByFrukt(String fruktType);
    List<ImmutableFruktkorg> listFruktkorgar();

    /**
     * This method does only update existing fruktkorgar. Uses fruktkorg-update.xsd to unmarshal.
     * @param inputStream
     * @return
     */
    List<ImmutableFruktkorg> updateFruktkorgar(InputStream inputStream);

    /**
     * This method can both create and edit fruktkorgar. After method call, database will be according to umarshalled
     * fruktkorgar.
     * @param inputStream
     * @return
     */
    List<ImmutableFruktkorg> restoreFruktkorgar(InputStream inputStream) throws FruktkorgMissingException, FruktMissingException;
}
