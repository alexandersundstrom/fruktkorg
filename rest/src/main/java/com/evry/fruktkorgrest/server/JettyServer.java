package com.evry.fruktkorgrest.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.Servlet;

public class JettyServer {
    private static final Logger logger = LogManager.getLogger(JettyServer.class);

    private Server server;
    private ServletHandler servletHandler;
    private ResourceHandler resourceHandler;

    public void init(int port) {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.setConnectors(new Connector[] {connector});

        HandlerList handlerList = new HandlerList();
        servletHandler = new ServletHandler();
        handlerList.addHandler(servletHandler);

        resourceHandler = new ResourceHandler();
        handlerList.addHandler(resourceHandler);

        server.setHandler(handlerList);
    }

    public void start() throws Exception {
        logger.info("Starting jetty server");
        server.start();
        server.join();
    }

    public void registerServlet(Servlet servlet, String url) {
        servletHandler.addServletWithMapping(new ServletHolder(servlet), url);
    }

    public void registerServlet(ServletHolder servletHolder, String url) {
        servletHandler.addServletWithMapping(servletHolder, url);
    }

    public void setHandler(Handler handler) {
        server.setHandler(handler);
    }

    public ResourceHandler getResourceHandler() {
        return resourceHandler;
    }

    public void stop() throws Exception {
        if(server.isRunning()) {
            logger.info("Stopping jetty server");
            server.stop();
        }
    }
}
