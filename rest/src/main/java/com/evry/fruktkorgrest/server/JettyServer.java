package com.evry.fruktkorgrest.server;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.Servlet;

public class JettyServer {
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
        server.start();
        server.join();
    }

    public void registerServlet(Servlet servlet, String url) {
        servletHandler.addServletWithMapping(new ServletHolder(servlet), url);
    }
}