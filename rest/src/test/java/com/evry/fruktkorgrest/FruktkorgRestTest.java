package com.evry.fruktkorgrest;

import com.evry.fruktkorgrest.controller.FruktkorgController;
import com.evry.fruktkorgrest.server.JettyServer;
import com.evry.fruktkorgrest.servlet.FruktkorgServlet;
import com.evry.fruktkorgservice.exception.FruktkorgMissingException;
import com.evry.fruktkorgservice.model.ImmutableFrukt;
import com.evry.fruktkorgservice.model.ImmutableFruktBuilder;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.model.ImmutableFruktkorgBuilder;
import com.evry.fruktkorgservice.service.FruktkorgService;
import com.evry.fruktkorgservice.service.FruktkorgServiceImpl;
import com.evry.fruktkorgservice.utils.ModelUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class FruktkorgRestTest {
    private static JettyServer jettyServer;
    private static final int PORT = 58645;
    private static Thread serverThread;
    private static FruktkorgService fruktkorgService;
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static OkHttpClient client;

    @BeforeAll
    static void init() throws Exception {
        client = new OkHttpClient.Builder().build();

        fruktkorgService = Mockito.mock(FruktkorgServiceImpl.class);
        FruktkorgController fruktkorgController = new FruktkorgController(fruktkorgService);
        Servlet servlet = new FruktkorgServlet(fruktkorgController);

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
                    .url("http://localhost:" + PORT + "/ping")
                    .get()
                    .build();
            try {
                client.newCall(request).execute();
                serverStarted = true;
            } catch (IOException e) {
                Thread.sleep(100);
            }
        } while(serverStarted);
    }

    @AfterAll
    static void cleanup() throws Exception {
        jettyServer.stop();
        serverThread.stop();
    }

    @Test
    void getFruktkorgTest() throws FruktkorgMissingException, IOException {
        Mockito.when(fruktkorgService.getFruktkorgById(1)).thenReturn(
                new ImmutableFruktkorgBuilder()
                .setId(1)
                .setName("Korg")
                .createImmutableFruktkorg()
        );

        Request request = new Request.Builder()
                .url("http://localhost:" + PORT + "/fruktkorg?id=1")
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(HttpServletResponse.SC_OK, response.code());
        Assertions.assertNotNull(response.body());
        ImmutableFruktkorg responseFruktkorg = objectMapper.readValue(response.body().string(), ImmutableFruktkorg.class);

        Assertions.assertEquals(1, responseFruktkorg.getId());
        Assertions.assertEquals("Korg", responseFruktkorg.getName());
        Assertions.assertEquals(0, responseFruktkorg.getFruktList().size());
    }

    @Test
    void getMissingFruktkorgTest() throws IOException, FruktkorgMissingException {
        Mockito.when(fruktkorgService.getFruktkorgById(1)).thenThrow(FruktkorgMissingException.class);

        Request request = new Request.Builder()
                .url("http://localhost:" + PORT + "/fruktkorg?id=1")
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.code());
        Assertions.assertNotNull(response.body());

        Map<String, Object> jsonResponse = objectMapper.readValue(response.body().string(), new TypeReference<HashMap<String,Object>>() {});

        Assertions.assertTrue(jsonResponse.containsKey("message"));
    }

    @Test
    void getFruktkorgMissingIdParameterTest() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:" + PORT + "/fruktkorg")
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.code());
        Assertions.assertNotNull(response.body());

        Map<String, Object> jsonResponse = objectMapper.readValue(response.body().string(), new TypeReference<HashMap<String,Object>>() {});

        Assertions.assertTrue(jsonResponse.containsKey("message"));
    }

    @Test
    void getFruktkorgUnparsableIdParameterTest() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:" + PORT + "/fruktkorg?id=very-wrong-id")
                .addHeader("Content-Type", "application/json")
                .get()
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.code());
        Assertions.assertNotNull(response.body());

        Map<String, Object> jsonResponse = objectMapper.readValue(response.body().string(), new TypeReference<HashMap<String,Object>>() {});

        Assertions.assertTrue(jsonResponse.containsKey("message"));
    }

    @Test
    void deleteFruktkorgTest() throws IOException {
        Request request = new Request.Builder()
                .url("http://localhost:" + PORT + "/fruktkorg?id=1")
                .addHeader("Content-Type", "application/json")
                .delete()
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(HttpServletResponse.SC_OK, response.code());
        Assertions.assertNotNull(response.body());
        Map<String, Object> jsonResponse = objectMapper.readValue(response.body().string(), new TypeReference<HashMap<String,Object>>() {});

        Assertions.assertTrue(jsonResponse.containsKey("message"));
    }

    @Test
    void deleteMissingFruktkorgTest() throws IOException, FruktkorgMissingException {
        Mockito.doThrow(FruktkorgMissingException.class).when(fruktkorgService).deleteFruktkorg(1);

        Request request = new Request.Builder()
                .url("http://localhost:" + PORT + "/fruktkorg?id=1")
                .addHeader("Content-Type", "application/json")
                .delete()
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(HttpServletResponse.SC_NOT_FOUND, response.code());
        Assertions.assertNotNull(response.body());
        Map<String, Object> jsonResponse = objectMapper.readValue(response.body().string(), new TypeReference<HashMap<String,Object>>() {});

        Assertions.assertTrue(jsonResponse.containsKey("message"));
    }

    @Test
    void createFruktkorgTest() throws IOException {
        Mockito.when(fruktkorgService.createFruktkorg(Mockito.any(ImmutableFruktkorg.class)))
                .thenReturn(new ImmutableFruktkorgBuilder()
                        .setId(1)
                        .setName("Korg")
                        .createImmutableFruktkorg()
                );

        ImmutableFruktkorg immutableFruktkorg = new ImmutableFruktkorgBuilder()
                .setName("Korg")
                .createImmutableFruktkorg();

        Request request = new Request.Builder()
                .url("http://localhost:" + PORT + "/create-fruktkorg")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.parse("application/json"), objectMapper.writeValueAsString(immutableFruktkorg)))
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(HttpServletResponse.SC_CREATED, response.code());
        Assertions.assertNotNull(response.body());
        ImmutableFruktkorg responseFruktkorg = objectMapper.readValue(response.body().string(), ImmutableFruktkorg.class);

        Assertions.assertEquals(1, responseFruktkorg.getId());
        Assertions.assertEquals(immutableFruktkorg.getName(), responseFruktkorg.getName());
        Assertions.assertEquals(immutableFruktkorg.getFruktList().size(), responseFruktkorg.getFruktList().size());
    }

    @Test
    void createFruktkorgMissingRequestBodyTest() throws IOException {
        Mockito.when(fruktkorgService.createFruktkorg(Mockito.any(ImmutableFruktkorg.class)))
                .thenReturn(new ImmutableFruktkorgBuilder()
                        .setId(1)
                        .setName("Korg")
                        .createImmutableFruktkorg()
                );

        ImmutableFruktkorg immutableFruktkorg = new ImmutableFruktkorgBuilder()
                .setName("Korg")
                .createImmutableFruktkorg();

        Request request = new Request.Builder()
                .url("http://localhost:" + PORT + "/create-fruktkorg")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.parse("application/json"), "{}"))
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(HttpServletResponse.SC_BAD_REQUEST, response.code());
        Assertions.assertNotNull(response.body());
        Map<String, Object> jsonResponse = objectMapper.readValue(response.body().string(), new TypeReference<HashMap<String,Object>>() {});

        Assertions.assertTrue(jsonResponse.containsKey("message"));
    }

    @Test
    void addFruktToFruktkorg() throws IOException, FruktkorgMissingException {
        final long fruktkorgId = 1;
        final String fruktType = "Banan";
        final int fruktAmount = 5;
        final long fruktId = 1;
        final String fruktkorgName = "Korg";

        ImmutableFrukt immutableFrukt = new ImmutableFruktBuilder()
                .setFruktkorgId(fruktkorgId)
                .setType(fruktType)
                .setAmount(fruktAmount)
                .createImmutableFrukt();

        ImmutableFrukt returnImmutableFrukt = new ImmutableFruktBuilder()
                .setFruktkorgId(fruktkorgId )
                .setType(fruktType)
                .setAmount(fruktAmount)
                .setId(fruktId)
                .createImmutableFrukt();

        Mockito.when(fruktkorgService.addFruktToFruktkorg(Mockito.anyLong(), Mockito.any(ImmutableFrukt.class)))
                .thenReturn(new ImmutableFruktkorgBuilder()
                        .setId(fruktkorgId)
                        .setName(fruktkorgName)
                        .addFrukt(returnImmutableFrukt)
                        .createImmutableFruktkorg()
                );


        Request request = new Request.Builder()
                .url("http://localhost:" + PORT + "/fruktkorg/add-frukt")
                .addHeader("Content-Type", "application/json")
                .put(RequestBody.create(MediaType.parse("application/json"), objectMapper.writeValueAsString(immutableFrukt)))
                .build();

        Response response = client.newCall(request).execute();

        Assertions.assertEquals(HttpServletResponse.SC_OK, response.code());
        Assertions.assertNotNull(response.body());
        ImmutableFruktkorg responseFruktkorg = objectMapper.readValue(response.body().string(), ImmutableFruktkorg.class);

        Assertions.assertEquals(fruktkorgId, responseFruktkorg.getId(), "Fruktkorg id should be the same");
        Assertions.assertEquals(fruktkorgName, responseFruktkorg.getName(), "Fruktkorg name should be the same");
        Assertions.assertEquals(1, responseFruktkorg.getFruktList().size(), "Size of Fruktkorg should be the same ");

        ImmutableFrukt responseFrukt = responseFruktkorg.getFruktList().get(0);
        Assertions.assertEquals(fruktId, responseFrukt.getId(), "Id of Frukt should be the same");
        Assertions.assertEquals(fruktType, responseFrukt.getType(), "Type of Frutk should be the same");
        Assertions.assertEquals(fruktAmount, responseFrukt.getAmount(), "Amount of Frukt should be the same");
        Assertions.assertEquals(fruktkorgId, responseFrukt.getFruktkorgId(), "id of the Fruktkorg should be the same");
    }
}
