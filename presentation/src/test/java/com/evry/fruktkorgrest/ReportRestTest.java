package com.evry.fruktkorgrest;

import com.evry.fruktkorgrest.controller.FruktController;
import com.evry.fruktkorgrest.controller.FruktkorgController;
import com.evry.fruktkorgrest.controller.ReportController;
import com.evry.fruktkorgrest.server.JettyServer;
import com.evry.fruktkorgrest.servlet.FruktkorgServlet;
import com.evry.fruktkorgservice.model.ImmutableReport;
import com.evry.fruktkorgservice.model.ImmutableReportBuilder;
import com.evry.fruktkorgservice.ReportService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

class ReportRestTest {
    private static JettyServer jettyServer;
    private static final int PORT = 58646;
    private static Thread serverThread;
    private static ReportService reportService;
    private static ObjectMapper objectMapper;
    private static OkHttpClient client;

    @BeforeAll
    static void init() throws Exception {
        client = new OkHttpClient.Builder().build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());

        reportService = Mockito.mock(ReportService.class);
        ReportController reportController = new ReportController(reportService);
        Servlet servlet = new FruktkorgServlet(Mockito.mock(FruktkorgController.class), Mockito.mock(FruktController.class), reportController);

        serverThread = new Thread(() -> {
            jettyServer = new JettyServer();
            jettyServer.init(PORT);
            jettyServer.registerServlet(servlet, "/*");
            try {
                jettyServer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        serverThread.start();

        int count = 0;
        while(jettyServer == null || !jettyServer.isStarted() || count++ < 25) {
            Thread.sleep(200);
        }
    }

    @AfterAll
    static void cleanup() throws Exception {
        jettyServer.stop();
        serverThread.stop();
    }

    @Test
    void getReportList() throws IOException {
        ImmutableReport immutableReport1 = new ImmutableReportBuilder()
                .setId(1)
                .setLocation("fake/location/report1.xmlconversion")
                .setCreated(Instant.now())
                .setRead(false)
                .createImmutableReport();

        ImmutableReport immutableReport2 = new ImmutableReportBuilder()
                .setId(2)
                .setLocation("fake/location/report2.xmlconversion")
                .setCreated(Instant.now().minus(4, ChronoUnit.DAYS))
                .setRead(false)
                .createImmutableReport();

        Mockito.when(reportService.listReports()).thenReturn(Arrays.asList(immutableReport1, immutableReport2));

        Request request = new Request.Builder()
                .url("http://localhost:" + PORT + "/rest/report-list")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(HttpServletResponse.SC_OK, response.code(), "Response should be OK");
        Assertions.assertNotNull(response.body(), "Request body should not be null");
        List<ImmutableReport> immutableReports = objectMapper.readValue(response.body().string(), new TypeReference<List<ImmutableReport>>() {
        });

        Assertions.assertEquals(2, immutableReports.size(), "Returned reports should be two");

        Assertions.assertEquals(immutableReport1.getId(), immutableReports.get(0).getId());
        Assertions.assertEquals(immutableReport1.getLocation(), immutableReports.get(0).getLocation());
        Assertions.assertEquals(immutableReport1.getCreated(), immutableReports.get(0).getCreated());
        Assertions.assertEquals(immutableReport1.isRead(), immutableReports.get(0).isRead());

        Assertions.assertEquals(immutableReport2.getId(), immutableReports.get(1).getId());
        Assertions.assertEquals(immutableReport2.getLocation(), immutableReports.get(1).getLocation());
        Assertions.assertEquals(immutableReport2.getCreated(), immutableReports.get(1).getCreated());
        Assertions.assertEquals(immutableReport2.isRead(), immutableReports.get(1).isRead());
    }

    @Test
    void getReportListWithOffsetAndLimit() throws IOException {
        ImmutableReport immutableReport1 = new ImmutableReportBuilder()
                .setId(1)
                .setLocation("fake/location/report1.xmlconversion")
                .setCreated(Instant.now())
                .setRead(false)
                .createImmutableReport();

        ImmutableReport immutableReport2 = new ImmutableReportBuilder()
                .setId(2)
                .setLocation("fake/location/report2.xmlconversion")
                .setCreated(Instant.now().minus(4, ChronoUnit.DAYS))
                .setRead(false)
                .createImmutableReport();

        Mockito.when(reportService.listReports(2, 0)).thenReturn(Arrays.asList(immutableReport1, immutableReport2));

        Request request = new Request.Builder()
                .url("http://localhost:" + PORT + "/rest/report-list?limit=2&offset=0")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(HttpServletResponse.SC_OK, response.code(), "Response should be OK");
        Assertions.assertNotNull(response.body(), "Request body should not be null");
        List<ImmutableReport> immutableReports = objectMapper.readValue(response.body().string(), new TypeReference<List<ImmutableReport>>() {
        });

        Assertions.assertEquals(2, immutableReports.size(), "Returned reports should be two");

        Assertions.assertEquals(immutableReport1.getId(), immutableReports.get(0).getId());
        Assertions.assertEquals(immutableReport1.getLocation(), immutableReports.get(0).getLocation());
        Assertions.assertEquals(immutableReport1.getCreated(), immutableReports.get(0).getCreated());
        Assertions.assertEquals(immutableReport1.isRead(), immutableReports.get(0).isRead());

        Assertions.assertEquals(immutableReport2.getId(), immutableReports.get(1).getId());
        Assertions.assertEquals(immutableReport2.getLocation(), immutableReports.get(1).getLocation());
        Assertions.assertEquals(immutableReport2.getCreated(), immutableReports.get(1).getCreated());
        Assertions.assertEquals(immutableReport2.isRead(), immutableReports.get(1).isRead());
    }
}
