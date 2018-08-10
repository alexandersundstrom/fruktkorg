package com.evry.fruktkorgrest.servlet;

import com.evry.fruktkorgservice.exception.FruktkorgMissingException;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.service.FruktkorgService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FruktkorgServlet extends HttpServlet {

    private FruktkorgService fruktkorgService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public FruktkorgServlet(FruktkorgService fruktkorgService) {
        this.fruktkorgService = fruktkorgService;
    }

    private static final Logger logger = LogManager.getLogger(FruktkorgServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI();
        resp.setContentType("application/json");

        switch(path) {
            case "/ping":
                ping(req, resp);
                break;
            case "/fruktkorg":
                getFruktkorg(req, resp);
                break;
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI();
        resp.setContentType("application/json");

        switch(path) {
            case "/fruktkorg":
                deleteFruktkorg(req, resp);
                break;
        }
    }

    private void getFruktkorg(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String stringId = req.getParameter("id");

        if(stringId == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"message\": \"Fruktkorg id parameter missing\"}");
            return;
        }

        if(!isLong(stringId)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"message\": \"Fruktkorg id has to be an integer\"}");
            return;
        }

        ImmutableFruktkorg fruktkorg;

        try {
            fruktkorg = fruktkorgService.getFruktkorgById(Long.valueOf(stringId));
        } catch (FruktkorgMissingException e) {
            logger.warn("Fruktkorg missing in getting", e);
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().print("{\"message\": \"" + e.getMessage() + "\"}");
            return;
        } catch (IllegalArgumentException e) {
            logger.warn("Fruktkorg id was illegal in getting", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"message\": \"" + e.getMessage() + "\"}");
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().print(objectMapper.writeValueAsString(fruktkorg));
    }

    private void deleteFruktkorg(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String stringId = req.getParameter("id");

        if(stringId == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"message\": \"Fruktkorg id parameter missing\"}");
            return;
        }

        if(!isLong(stringId)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"message\": \"Fruktkorg id has to be an integer\"}");
            return;
        }

        try {
            fruktkorgService.deleteFruktkorg(Long.valueOf(stringId));
        } catch (FruktkorgMissingException e) {
            logger.warn("Fruktkorg missing in deletion", e);
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().print("{\"message\": \"" + e.getMessage() + "\"}");
            return;
        } catch (IllegalArgumentException e) {
            logger.warn("Fruktkorg id was illegal in deletion", e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"message\": \"" + e.getMessage() + "\"}");
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().print("{\"message\": \"Fruktkorg with id " + stringId + " was deleted\"}");
    }

    private void ping(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().print("{\"message\": \"pong\"}");
    }

    private boolean isLong(String number) {
        try {
            Long.valueOf(number);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
