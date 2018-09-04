package com.evry.fruktkorgpersistence.model;

import java.util.List;
import java.util.Optional;

public interface ReportRepository {
    void persist(Report report);

    Report merge(Report report);

    void remove(Report report);

    List<Report> findAll();

    List<Report> findAllByLimitAndOffset(int limit, int offset);

    Optional<Report> findById(long id);

    void removeByRead();

    List<Report> getAllByRead();


}
