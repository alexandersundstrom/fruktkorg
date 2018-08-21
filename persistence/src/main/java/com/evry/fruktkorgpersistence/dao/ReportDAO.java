package com.evry.fruktkorgpersistence.dao;

import com.evry.fruktkorgpersistence.model.Report;

import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

public interface ReportDAO {
    void persist(Report report);
    Report merge(Report report);
    void remove(Report report);

    List<Report> listReports();
    List<Report> listReports(int limit, int offset);
    Optional<Report> findReportById(long id);
    void setEntityManagerFactory(EntityManagerFactory entityManagerFactory);

    void removeReadReports();
    List<Report> getReadReports();
}
