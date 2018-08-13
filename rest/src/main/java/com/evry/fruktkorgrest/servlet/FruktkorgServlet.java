package com.evry.fruktkorgrest.servlet;

import com.evry.fruktkorgrest.controller.FruktkorgController;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FruktkorgServlet extends HttpServlet {
    private FruktkorgController fruktkorgController;
    private static final Logger logger = LogManager.getLogger(FruktkorgServlet.class);

    public FruktkorgServlet(FruktkorgController fruktkorgController) {
        this.fruktkorgController = fruktkorgController;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getRequestURI();
        resp.setContentType("application/json");

        switch(path) {
            case "/ping":
                ping(req, resp);
                break;
            case "/fruktkorg":
                logger.debug("Got request to get fruktkorg");
                fruktkorgController.getFruktkorg(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getRequestURI();
        resp.setContentType("application/json");

        switch(path) {
            case "/create-fruktkorg":
                logger.debug("Got request to create fruktkorg");
                fruktkorgController.createFruktkorg(req, resp);
                break;
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getRequestURI();
        resp.setContentType("application/json");

        switch(path) {
            case "/fruktkorg":
                logger.debug("Got request to delete fruktkorg");
                fruktkorgController.deleteFruktkorg(req, resp);
                break;

            case "/fruktkorg/":
                //logger.debug("Got request to delete fruktkorg");
                //fruktkorgController.deleteFruktkorg(req, resp);
                break;
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getRequestURI();
        resp.setContentType("application/json");

        switch(path) {
            case "/fruktkorg/add-frukt":
                logger.debug("Got request to add frukt to fruktkorg");
                fruktkorgController.addFruktToFruktkorg(req, resp);
                break;
        }
    }

    private void ping(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().print("{\"message\": \"pong\"}");
    }
}
