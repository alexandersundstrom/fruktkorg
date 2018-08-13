package com.evry.fruktkorgrest.server;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.Servlet;

public class JettyServer {
    private static final Logger logger = LogManager.getLogger(JettyServer.class);

    private Server server;
    private ServletHandler servletHandler;

    public void init(int port) {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.setConnectors(new Connector[] {connector});
        servletHandler = new ServletHandler();
        server.setHandler(servletHandler);
    }

    public void start() throws Exception {
        logger.info("Starting jetty server");
        server.start();
        server.join();
    }

    public void registerServlet(Servlet servlet, String url) {
        servletHandler.addServletWithMapping(new ServletHolder(servlet), url);
    }

    public void stop() throws Exception {
        if(server.isRunning()) {
            logger.info("Stopping jetty server");
            server.stop();
        }
    }
}
