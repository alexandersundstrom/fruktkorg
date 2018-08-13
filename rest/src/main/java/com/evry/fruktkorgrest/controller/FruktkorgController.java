package com.evry.fruktkorgrest.controller;

import com.evry.fruktkorgservice.exception.FruktkorgMissingException;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.service.FruktkorgService;
import com.evry.fruktkorgservice.utils.NumberUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FruktkorgController {

    private FruktkorgService fruktkorgService;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LogManager.getLogger(FruktkorgController.class);

    public FruktkorgController (FruktkorgService fruktkorgService) {
        this.fruktkorgService = fruktkorgService;
    }

    public void getFruktkorg(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String stringId = req.getParameter("id");

        if(stringId == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"message\": \"Fruktkorg id parameter missing\"}");
            return;
        }

        if(!NumberUtils.isLong(stringId)) {
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

    public void deleteFruktkorg(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String stringId = req.getParameter("id");

        if(stringId == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"message\": \"Fruktkorg id parameter missing\"}");
            return;
        }

        if(!NumberUtils.isLong(stringId)) {
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

}
