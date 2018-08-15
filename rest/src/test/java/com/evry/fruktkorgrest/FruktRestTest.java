package com.evry.fruktkorgrest;

import com.evry.fruktkorgrest.controller.FruktController;
import com.evry.fruktkorgrest.controller.FruktkorgController;
import com.evry.fruktkorgrest.controller.ReportController;
import com.evry.fruktkorgrest.server.JettyServer;
import com.evry.fruktkorgrest.servlet.FruktkorgServlet;
import com.evry.fruktkorgservice.service.FruktService;
import com.evry.fruktkorgservice.service.FruktServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Arrays;
import java.util.List;

class FruktRestTest {
    private static JettyServer jettyServer;
    private static final int PORT = 58646;
    private static Thread serverThread;
    private static FruktService fruktService;
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static OkHttpClient client;

    @BeforeAll
    static void init() throws Exception {
        client = new OkHttpClient.Builder().build();

        fruktService = Mockito.mock(FruktServiceImpl.class);
        FruktController fruktController = new FruktController(fruktService);
        Servlet servlet = new FruktkorgServlet(Mockito.mock(FruktkorgController.class), fruktController, Mockito.mock(ReportController.class));

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

        boolean serverStarted = false;
        do {
            Request request = new Request.Builder()
                    .url("http://localhost:" + PORT + "/rest/ping")
                    .get()
                    .build();
            try {
                client.newCall(request).execute();
                serverStarted = true;
            } catch (IOException e) {
                Thread.sleep(100);
            }
        } while (serverStarted);
    }

    @AfterAll
    static void cleanup() throws Exception {
        jettyServer.stop();
        serverThread.stop();
    }

    @Test
    void getUniqueFruktTypes() throws IOException {
        Mockito.when(fruktService.getUniqueFruktTypes()).thenReturn(Arrays.asList("Banan", "Äpple"));

        Request request = new Request.Builder()
                .url("http://localhost:" + PORT + "/rest/frukt/unique-types")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(HttpServletResponse.SC_OK, response.code(), "Response should be OK");
        Assertions.assertNotNull(response.body(), "Request body should not be null");
        List<String> uniqueTypes = objectMapper.readValue(response.body().string(), new TypeReference<List<String>>() {});

        Assertions.assertEquals(2, uniqueTypes.size(), "Returned types should be two");
        Assertions.assertEquals(Arrays.asList("Banan", "Äpple"), uniqueTypes, "Returned types should contain the correct Frukt");
    }
}
