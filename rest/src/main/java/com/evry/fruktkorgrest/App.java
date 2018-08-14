package com.evry.fruktkorgrest;

import com.evry.fruktkorgrest.server.JettyServer;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.Servlet;

public class App {
    public static void main(String args[]) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");

        JettyServer jettyServer = new JettyServer();

        jettyServer.init(8080);

        String webResourceDirectory = args[0];

        ServletContextHandler servletContextHandler = new ServletContextHandler(
                ServletContextHandler.SESSIONS);
        servletContextHandler.setContextPath("/");
        servletContextHandler.setBaseResource(Resource.newResource(webResourceDirectory));
        servletContextHandler.setWelcomeFiles(new String[] { "index.html" });

        ServletHolder holderPwd = new ServletHolder("default",
                DefaultServlet.class);
        holderPwd.setInitParameter("dirAllowed", "true");
        servletContextHandler.addServlet(new ServletHolder((Servlet)context.getBean("fruktkorgServlet")), "/rest/*");
        servletContextHandler.addServlet(holderPwd, "/");

        jettyServer.setHandler(servletContextHandler);

        jettyServer.start();
    }
}
