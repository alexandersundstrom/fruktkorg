package com.evry.fruktkorgrest.controller;

import com.evry.fruktkorgrest.utils.NumberUtils;
import com.evry.fruktkorgservice.exception.FruktkorgMissingException;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.service.FruktkorgService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FruktkorgController {

    private FruktkorgService fruktkorgService;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LogManager.getLogger(FruktkorgController.class);

    public FruktkorgController (FruktkorgService fruktkorgService) {
        this.fruktkorgService = fruktkorgService;
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
    }

    public void getFruktkorg(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String stringId = req.getParameter("id");

        if(isIdInvalid(stringId, resp)) {
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

        if(isIdInvalid(stringId, resp)) {
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

    public void createFruktkorg(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ImmutableFruktkorg immutableFruktkorg = objectMapper.readValue(req.getReader(), ImmutableFruktkorg.class);

        if(StringUtils.isEmpty(immutableFruktkorg.getName())) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"message\": \"The name has to be set\"}");
            return;
        }

        ImmutableFruktkorg createdFruktkorg = fruktkorgService.createFruktkorg(immutableFruktkorg);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().print(objectMapper.writeValueAsString(createdFruktkorg));
    }

    private boolean isIdInvalid(String id, HttpServletResponse response) throws IOException {
        if(id == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"message\": \"Fruktkorg id parameter missing\"}");
            return true;
        }

        if(!NumberUtils.isLong(id)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"message\": \"Fruktkorg id has to be an integer\"}");
            return true;
        }

        return false;
    }
}
