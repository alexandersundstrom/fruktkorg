package com.evry.fruktkorgservice;

import com.evry.fruktkorgpersistence.dao.ReportDAO;
import com.evry.fruktkorgpersistence.model.Report;
import com.evry.fruktkorgservice.exception.ReportMissingException;
import com.evry.fruktkorgservice.model.*;
import com.evry.fruktkorgservice.service.FruktkorgService;
import com.evry.fruktkorgservice.service.ReportService;
import com.evry.fruktkorgservice.service.ReportServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class ReportServiceTest {
    private ReportDAO reportDAO;
    private FruktkorgService fruktkorgService;
    private ReportService reportService;

    private final String TEST_XML = "" +
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<fruktkorgar>\n" +
            "    <fruktkorg>\n" +
            "        <id>1</id>\n" +
            "        <name>Korg 1</name>\n" +
            "        <frukt>\n" +
            "            <type>Banan</type>\n" +
            "            <amount>5</amount>\n" +
            "        </frukt>\n" +
            "        <frukt>\n" +
            "            <type>Kiwi</type>\n" +
            "            <amount>1</amount>\n" +
            "        </frukt>\n" +
            "        <frukt>\n" +
            "            <type>Ananas</type>\n" +
            "            <amount>9</amount>\n" +
            "        </frukt>\n" +
            "        <lastChanged>\n" +
            "            <epochMilli>505014000</epochMilli>\n" +
            "        </lastChanged>\n" +
            "    </fruktkorg>\n" +
            "    <fruktkorg>\n" +
            "        <id>2</id>\n" +
            "        <name>Korg 2</name>\n" +
            "        <frukt>\n" +
            "            <type>Päron</type>\n" +
            "            <amount>4</amount>\n" +
            "        </frukt>\n" +
            "        <frukt>\n" +
            "            <type>Äpple</type>\n" +
            "            <amount>4</amount>\n" +
            "        </frukt>\n" +
            "        <lastChanged>\n" +
            "            <epochMilli>249953000</epochMilli>\n" +
            "        </lastChanged>\n" +
            "    </fruktkorg>\n" +
            "</fruktkorgar>";

    @BeforeEach
    void init() {
        fruktkorgService = Mockito.mock(FruktkorgService.class);
        reportDAO = Mockito.mock(ReportDAO.class);
        reportService = new ReportServiceImpl(reportDAO, fruktkorgService);
        System.setProperty("file.encoding", "UTF-8");
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
    void listReportsWithLimitAndOffset() {
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

        Mockito.when(reportDAO.listReports(2, 0)).thenReturn(Arrays.asList(report1, report2));

        List<ImmutableReport> immutableReports = reportService.listReports(2, 0);

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
    void getAndMarkReport() throws ReportMissingException {
        Instant created = Instant.now().minus(4, ChronoUnit.DAYS);

        Report report = new Report();
        report.setId(1);
        report.setCreated(created);
        report.setLocation("fake/location/test/report.xml");
        report.setRead(false);

        Mockito.when(reportDAO.findReportById(1)).thenReturn(Optional.of(report));
        Mockito.when(reportDAO.merge(Mockito.any(Report.class))).thenReturn(report);

        ImmutableReport immutableReport = reportService.getAndMarkReport(1);

        Assertions.assertEquals(1, immutableReport.getId());
        Assertions.assertEquals("fake/location/test/report.xml", immutableReport.getLocation());
        Assertions.assertTrue(immutableReport.isRead());
        Assertions.assertEquals(created, immutableReport.getCreated());
    }

    @Test
    void getMissingReportById() {
        Mockito.when(reportDAO.findReportById(2)).thenReturn(Optional.empty());

        Assertions.assertThrows(ReportMissingException.class, () -> {
            reportService.getAndMarkReport(2);
        });
    }

    @Test
    void createReport() throws IOException {
        Instant now = Instant.now();

        File reportFile = File.createTempFile("test-report-", ".xml");
        reportFile.deleteOnExit();

        Mockito.doAnswer(invocationOnMock -> {
            Object[] arguments = invocationOnMock.getArguments();
            Report report = (Report)arguments[0];
            report.setId(1);
            report.setLocation(reportFile.getAbsolutePath());
            report.setCreated(now);
            report.setRead(false);

            return null;
        }).when(reportDAO).persist(Mockito.any(Report.class));

        Mockito.when(fruktkorgService.listFruktkorgar()).thenReturn(Arrays.asList(
                new ImmutableFruktkorgBuilder()
                    .setId(1)
                    .setName("Korg 1")
                    .setLastChanged(Instant.now().minus(4, ChronoUnit.DAYS))
                    .addFrukt(new ImmutableFruktBuilder()
                        .setId(1)
                        .setType("Banan")
                        .setAmount(4)
                        .setFruktkorgId(1)
                        .createImmutableFrukt()
                    ).createImmutableFruktkorg(),
                new ImmutableFruktkorgBuilder()
                        .setId(2)
                        .setName("Korg 2")
                        .setLastChanged(Instant.now().minus(6, ChronoUnit.DAYS))
                        .addFrukt(new ImmutableFruktBuilder()
                                .setId(2)
                                .setType("Äpple")
                                .setAmount(2)
                                .setFruktkorgId(2)
                                .createImmutableFrukt()
                        ).createImmutableFruktkorg()
        ));


        ImmutableReport immutableReport = reportService.createReport(reportFile.getAbsolutePath());

        Assertions.assertEquals(1, immutableReport.getId());
        Assertions.assertEquals(reportFile.getAbsolutePath(), immutableReport.getLocation());
        Assertions.assertEquals(now, immutableReport.getCreated());
        Assertions.assertFalse(immutableReport.isRead());
    }

    @Test
    void getFruktkorgarFromReport() throws ReportMissingException, IOException {
        File reportFile = File.createTempFile("test-report-", ".xml");
        reportFile.deleteOnExit();
        Files.write(Paths.get(reportFile.getAbsolutePath()), TEST_XML.getBytes(Charset.forName("UTF-8")));

        Report report = new Report();
        report.setRead(true);
        report.setCreated(Instant.now().minus(4, ChronoUnit.DAYS));
        report.setLocation(reportFile.getAbsolutePath());

        Mockito.when(reportDAO.findReportById(1)).thenReturn(Optional.of(report));

        List<ImmutableFruktkorg> immutableFruktkorgList = reportService.getFruktkorgarFromReport(1);

        Assertions.assertEquals(2, immutableFruktkorgList.size());

        ImmutableFruktkorg immutableFruktkorg1 = immutableFruktkorgList.get(0);
        ImmutableFruktkorg immutableFruktkorg2 = immutableFruktkorgList.get(1);
        Assertions.assertEquals(3, immutableFruktkorg1.getFruktList().size());
        Assertions.assertEquals(2, immutableFruktkorg2.getFruktList().size());

        ImmutableFrukt immutableBanan = immutableFruktkorg1.getFruktList().get(0);
        ImmutableFrukt immutableKiwi = immutableFruktkorg1.getFruktList().get(1);
        ImmutableFrukt immutableAnanas = immutableFruktkorg1.getFruktList().get(2);

        Assertions.assertEquals("Banan", immutableBanan.getType());
        Assertions.assertEquals(5, immutableBanan.getAmount());

        Assertions.assertEquals("Kiwi", immutableKiwi.getType());
        Assertions.assertEquals(1, immutableKiwi.getAmount());

        Assertions.assertEquals("Ananas", immutableAnanas.getType());
        Assertions.assertEquals(9, immutableAnanas.getAmount());

        ImmutableFrukt immutableParon = immutableFruktkorg2.getFruktList().get(0);
        ImmutableFrukt immutableApple = immutableFruktkorg2.getFruktList().get(1);

        Assertions.assertEquals("Päron", immutableParon.getType());
        Assertions.assertEquals(4, immutableParon.getAmount());

        Assertions.assertEquals("Äpple", immutableApple.getType());
        Assertions.assertEquals(4, immutableApple.getAmount());
    }

    @Test
    void removeReport() throws IOException, ReportMissingException {
        File reportFile = File.createTempFile("test-report-", ".xml");
        reportFile.deleteOnExit();
        Files.write(Paths.get(reportFile.getAbsolutePath()), TEST_XML.getBytes());

        Report report = new Report();
        report.setId(1);
        report.setRead(true);
        report.setCreated(Instant.now().minus(4, ChronoUnit.DAYS));
        report.setLocation(reportFile.getAbsolutePath());

        Mockito.when(reportDAO.findReportById(1)).thenReturn(Optional.of(report));

        reportService.removeReport(report.getId());

        Assertions.assertFalse(reportFile.exists());
    }

    @Test
    void removeReadReports() throws IOException {
        File reportFile = File.createTempFile("test-report-", ".xml");
        reportFile.deleteOnExit();
        Files.write(Paths.get(reportFile.getAbsolutePath()), TEST_XML.getBytes());

        Report report = new Report();
        report.setId(1);
        report.setRead(true);
        report.setCreated(Instant.now().minus(4, ChronoUnit.DAYS));
        report.setLocation(reportFile.getAbsolutePath());

        Mockito.when(reportDAO.getReadReports()).thenReturn(Collections.singletonList(report));

        reportService.removeReadReports();

        Assertions.assertFalse(reportFile.exists());
    }
}
