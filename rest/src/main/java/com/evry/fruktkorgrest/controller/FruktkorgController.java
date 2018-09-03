package com.evry.fruktkorgrest.controller;

import com.evry.fruktkorgrest.model.FruktkorgDTO;
import com.evry.fruktkorgrest.utils.NumberUtils;
import com.evry.fruktkorgservice.exception.FruktMissingException;
import com.evry.fruktkorgservice.exception.FruktkorgMissingException;
import com.evry.fruktkorgservice.domain.model.ImmutableFrukt;
import com.evry.fruktkorgservice.domain.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.domain.service.FruktkorgService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
        resp.getWriter().print(objectMapper.writeValueAsString(new FruktkorgDTO(fruktkorg)));
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
        resp.getWriter().print(objectMapper.writeValueAsString(new FruktkorgDTO(createdFruktkorg)));
    }

    public void addFruktToFruktkorg(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ImmutableFrukt immutableFrukt = objectMapper.readValue(req.getReader(), ImmutableFrukt.class);

        if (immutableFrukt == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"message\": \"A Frukt has to be provided\"}");
            return;
        }

        if (StringUtils.isEmpty(immutableFrukt.getType())) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"message\": \"The type of the Frukt has to be set\"}");
            return;
        }

        try {
            ImmutableFruktkorg immutableFruktkorg = fruktkorgService.addFruktToFruktkorg(immutableFrukt.getId(), immutableFrukt);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(objectMapper.writeValueAsString(new FruktkorgDTO(immutableFruktkorg)));
        } catch (FruktkorgMissingException e) {
            logger.warn(e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().print("{\"message\": \"Fruktkorg with id " + immutableFrukt.getFruktkorgId() + " was not found\"}");
        }
    }

    public void removeFruktFromFruktkorg(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fruktkorgId = req.getParameter("fruktkorgId");
        String fruktType = req.getParameter("fruktType");
        String fruktAmount = req.getParameter("fruktAmount");


        if(fruktkorgId == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"message\": \"Missing fruktkorg id\"}");
            return;
        }

        if(!NumberUtils.isLong(fruktkorgId)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"message\": \"Fruktkorg id has to be an integer\"}");
            return;
        }

        if(fruktType == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"message\": \"Missing frukt type\"}");
            return;
        }

        if(fruktAmount == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"message\": \"Missing amount\"}");
            return;
        }

        if(!NumberUtils.isInteger(fruktAmount)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"message\": \"Frukt amount has to be an integer\"}");
            return;
        }

        ImmutableFruktkorg immutableFruktkorg;
        try {
            immutableFruktkorg = fruktkorgService.removeFruktFromFruktkorg(Long.parseLong(fruktkorgId), fruktType, Integer.valueOf(fruktAmount));
        } catch (FruktkorgMissingException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().print("{\"message\": \"Unable find fruktkorg with id " + fruktkorgId + "\"}");
            return;
        } catch (FruktMissingException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().print("{\"message\": \"Unable find frukt with type " + fruktType + " in fruktkorg with id " + fruktkorgId + "\"}");
            return;
        }

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().print(objectMapper.writeValueAsString(new FruktkorgDTO(immutableFruktkorg)));
    }

    public void searchFruktkorg(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String fruktType = req.getParameter("fruktType");

        if(StringUtils.isEmpty(fruktType)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"message\": \"Frukt type parameter missing\"}");
            return;
        }

        List<ImmutableFruktkorg> immutableFruktkorgList = fruktkorgService.searchFruktkorgByFrukt(fruktType);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().print(objectMapper.writeValueAsString(immutableFruktkorgList
                .stream()
                .map(FruktkorgDTO::new)
                .collect(Collectors.toList())));
    }

    public void getFruktkorgList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().print(objectMapper.writeValueAsString(fruktkorgService.listFruktkorgar()
                .stream()
                .map(FruktkorgDTO::new)
                .collect(Collectors.toList())));
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
