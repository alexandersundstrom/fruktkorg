package com.evry.fruktkorgpersistence;


import com.evry.fruktkorgpersistence.model.Fruktkorg;

import java.util.List;

public interface FruktkorgService {
    void persist(Fruktkorg fruktkorg);
    void remove(Fruktkorg fruktkorg);
    void remove(long fruktkorgId);
    Fruktkorg merge(Fruktkorg fruktkorg);
    void refresh(Fruktkorg fruktkorg);

    List<Fruktkorg> listFruktkorg();
    List<Fruktkorg> findFruktkorgByFrukt();
}
