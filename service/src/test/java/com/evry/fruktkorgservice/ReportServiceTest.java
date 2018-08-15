package com.evry.fruktkorgservice;

import com.evry.fruktkorgpersistence.dao.ReportDAO;
import com.evry.fruktkorgpersistence.model.Report;
import com.evry.fruktkorgservice.exception.ReportMissingException;
import com.evry.fruktkorgservice.model.ImmutableReport;
import com.evry.fruktkorgservice.service.ReportService;
import com.evry.fruktkorgservice.service.ReportServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class ReportServiceTest {
    private ReportDAO reportDAO;
    private ReportService reportService;

    @BeforeEach
    void init() {
        reportDAO = Mockito.mock(ReportDAO.class);
        reportService = new ReportServiceImpl(reportDAO);
    }

    @Test
    void listReports() {
        Instant created1 = Instant.now();
        Instant created2 = Instant.now().minus(4, ChronoUnit.DAYS);

        Report report1 = new Report();
        report1.setId(1);
        report1.setCreated(created1);
        report1.setLocation("fake/location/test/report1.xml");
        report1.setRead(false);

        Report report2 = new Report();
        report2.setId(2);
        report2.setCreated(created2);
        report2.setLocation("fake/location/test/report2.xml");
        report2.setRead(false);

        Mockito.when(reportDAO.listReports()).thenReturn(Arrays.asList(report1, report2));

        List<ImmutableReport> immutableReports = reportService.listReports();

        Assertions.assertEquals(2, immutableReports.size());
        ImmutableReport immutableReport1 = immutableReports.get(0);

        Assertions.assertEquals(1, immutableReport1.getId());
        Assertions.assertEquals("fake/location/test/report1.xml", immutableReport1.getLocation());
        Assertions.assertFalse(immutableReport1.isRead());
        Assertions.assertEquals(created1, immutableReport1.getCreated());

        ImmutableReport immutableReport2 = immutableReports.get(1);
        Assertions.assertEquals(2, immutableReport2.getId());
        Assertions.assertEquals("fake/location/test/report2.xml", immutableReport2.getLocation());
        Assertions.assertFalse(immutableReport2.isRead());
        Assertions.assertEquals(created2, immutableReport2.getCreated());
    }

    @Test
    void getReportById() throws ReportMissingException {
        Instant created2 = Instant.now().minus(4, ChronoUnit.DAYS);

        Report report2 = new Report();
        report2.setId(2);
        report2.setCreated(created2);
        report2.setLocation("fake/location/test/report2.xml");
        report2.setRead(false);

        Mockito.when(reportDAO.findReportById(2)).thenReturn(Optional.of(report2));

        ImmutableReport immutableReport = reportService.getReportById(2);

        Assertions.assertEquals(2, immutableReport.getId());
        Assertions.assertEquals("fake/location/test/report2.xml", immutableReport.getLocation());
        Assertions.assertFalse(immutableReport.isRead());
        Assertions.assertEquals(created2, immutableReport.getCreated());
    }

    @Test
    void getMissingReportById() {
        Mockito.when(reportDAO.findReportById(2)).thenReturn(Optional.empty());

        Assertions.assertThrows(ReportMissingException.class, () -> {
            reportService.getReportById(2);
        });
    }
}
