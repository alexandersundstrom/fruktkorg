package com.evry.fruktkorgpersistence;

import com.evry.fruktkorgpersistence.hibernate.ReportRepositoryHibernate;
import com.evry.fruktkorgpersistence.model.Report;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.Instant;
import java.util.List;

class ReportTest {
    private static ReportRepositoryHibernate reportRepository;

    @BeforeEach
    void init() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test");

        reportRepository = new ReportRepositoryHibernate();
        reportRepository.setEntityManagerFactory(entityManagerFactory);
    }

    @Test
    void persistReport() {
        Report report = new Report();
        report.setCreated(Instant.now());
        report.setLocation("fake/location/test/report.xml");
        report.setRead(false);

        reportRepository.persist(report);

        report = reportRepository.findById(1).orElse(null);

        Assertions.assertNotNull(report);
        Assertions.assertEquals(1, report.getId());
        Assertions.assertFalse(report.isRead());
        Assertions.assertNotNull(report.getCreated());
        Assertions.assertEquals("fake/location/test/report.xml", report.getLocation());
    }

    @Test
    void mergeReport() {
        Report report = new Report();
        report.setCreated(Instant.now());
        report.setLocation("fake/location/test/report.xml");
        report.setRead(false);

        reportRepository.persist(report);

        report = reportRepository.findById(1).orElse(null);

        Assertions.assertNotNull(report);

        report.setRead(true);

        report = reportRepository.merge(report);

        Assertions.assertTrue(report.isRead());
        Assertions.assertNotNull(report.getCreated());
        Assertions.assertEquals("fake/location/test/report.xml", report.getLocation());
    }

    @Test
    void removeReport() {
        Report report = new Report();
        report.setCreated(Instant.now());
        report.setLocation("fake/location/test/report.xml");
        report.setRead(false);

        reportRepository.persist(report);

        report = reportRepository.findById(1).orElse(null);

        Assertions.assertNotNull(report);

        reportRepository.remove(report);

        Assertions.assertFalse(reportRepository.findById(1).isPresent());
    }

    @Test
    void listReport() {
        Report report1 = new Report();
        report1.setCreated(Instant.now());
        report1.setLocation("fake/location/test/report1.xml");
        report1.setRead(false);

        reportRepository.persist(report1);

        Report report2 = new Report();
        report2.setCreated(Instant.now());
        report2.setLocation("fake/location/test/report2.xml");
        report2.setRead(false);

        reportRepository.persist(report2);

        Report report3 = new Report();
        report3.setCreated(Instant.now());
        report3.setLocation("fake/location/test/report2.xml");
        report3.setRead(false);

        reportRepository.persist(report3);

        List<Report> reportList = reportRepository.findAll();

        Assertions.assertEquals(3, reportList.size());
        Assertions.assertEquals(report1, reportList.get(0));
        Assertions.assertEquals(report2, reportList.get(1));
        Assertions.assertEquals(report3, reportList.get(2));
    }

    @Test
    void findReportById() {
        Report report1 = new Report();
        report1.setCreated(Instant.now());
        report1.setLocation("fake/location/test/report1.xml");
        report1.setRead(false);

        reportRepository.persist(report1);

        Report report2 = new Report();
        report2.setCreated(Instant.now());
        report2.setLocation("fake/location/test/report2.xml");
        report2.setRead(false);

        reportRepository.persist(report2);

        Report report3 = new Report();
        report3.setCreated(Instant.now());
        report3.setLocation("fake/location/test/report2.xml");
        report3.setRead(false);

        reportRepository.persist(report3);

        Report report = reportRepository.findById(2).orElse(null);

        Assertions.assertNotNull(report);
        Assertions.assertEquals(2, report.getId());
        Assertions.assertFalse(report.isRead());
        Assertions.assertNotNull(report.getCreated());
        Assertions.assertEquals("fake/location/test/report2.xml", report.getLocation());
    }

    @Test
    void listReportWithLimitAndOffset() {
        Report report1 = new Report();
        report1.setCreated(Instant.now());
        report1.setLocation("fake/location/test/report1.xml");
        report1.setRead(false);

        reportRepository.persist(report1);

        Report report2 = new Report();
        report2.setCreated(Instant.now());
        report2.setLocation("fake/location/test/report2.xml");
        report2.setRead(false);

        reportRepository.persist(report2);

        Report report3 = new Report();
        report3.setCreated(Instant.now());
        report3.setLocation("fake/location/test/report2.xml");
        report3.setRead(false);

        reportRepository.persist(report3);

        List<Report> reportList = reportRepository.findAllByLimitAndOffset(1, 0);
        Assertions.assertEquals(1, reportList.size());
        Assertions.assertEquals(report1, reportList.get(0));

        reportList = reportRepository.findAllByLimitAndOffset(1, 1);
        Assertions.assertEquals(1, reportList.size());
        Assertions.assertEquals(report2, reportList.get(0));

        reportList = reportRepository.findAllByLimitAndOffset(3, 0);
        Assertions.assertEquals(3, reportList.size());
    }

    @Test
    void removeReadReports() {
        Report report1 = new Report();
        report1.setCreated(Instant.now());
        report1.setLocation("fake/location/test/report1.xml");
        report1.setRead(false);

        reportRepository.persist(report1);

        Report report2 = new Report();
        report2.setCreated(Instant.now());
        report2.setLocation("fake/location/test/report2.xml");
        report2.setRead(true);

        reportRepository.persist(report2);

        Report report3 = new Report();
        report3.setCreated(Instant.now());
        report3.setLocation("fake/location/test/report3.xml");
        report3.setRead(false);

        reportRepository.persist(report3);

        reportRepository.removeByRead();

        List<Report> reportList = reportRepository.findAll();

        Assertions.assertEquals(2, reportList.size());
    }

    @Test
    void getReadReports() {
        Report report1 = new Report();
        report1.setCreated(Instant.now());
        report1.setLocation("fake/location/test/report1.xml");
        report1.setRead(false);

        reportRepository.persist(report1);

        Report report2 = new Report();
        report2.setCreated(Instant.now());
        report2.setLocation("fake/location/test/report2.xml");
        report2.setRead(true);

        reportRepository.persist(report2);

        Report report3 = new Report();
        report3.setCreated(Instant.now());
        report3.setLocation("fake/location/test/report3.xml");
        report3.setRead(false);

        reportRepository.persist(report3);

        List<Report> readReports = reportRepository.getAllByRead();

        Assertions.assertEquals(1, readReports.size());
    }
}
