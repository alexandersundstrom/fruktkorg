package com.evry.fruktkorgservice.service;


import com.evry.fruktkorgservice.model.ImmutableFrukt;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;

public interface FruktkorgService {
    ImmutableFruktkorg createFruktkorg(ImmutableFruktkorg immutableFruktkorg);
    void deleteFruktkorg(long fruktkorgId);
    ImmutableFruktkorg addFruktToFruktkorg(long fruktkorgId, ImmutableFrukt immutableFrukt);
}
