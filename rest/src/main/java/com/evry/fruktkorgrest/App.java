package com.evry.fruktkorgrest;

import com.evry.fruktkorgrest.server.JettyServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.Servlet;

public class App {
    public static void main(String args[]) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");

        JettyServer jettyServer = new JettyServer();

        jettyServer.init(8080);
//        jettyServer.registerServlet(FruktkorgServlet.class, "/*");
        jettyServer.registerServlet((Servlet)context.getBean("fruktkorgServlet"), "/*");
        jettyServer.start();
    }
}
