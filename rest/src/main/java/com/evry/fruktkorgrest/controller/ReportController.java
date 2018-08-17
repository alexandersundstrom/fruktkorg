package com.evry.fruktkorgrest.controller;

import com.evry.fruktkorgrest.utils.NumberUtils;
import com.evry.fruktkorgservice.service.ReportService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.omg.PortableInterceptor.INACTIVE;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ReportController {
    private ReportService reportService;
    private ObjectMapper objectMapper = new ObjectMapper();

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        objectMapper.registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());
    }

    public void getReportList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String limit = req.getParameter("limit");
        String offset = req.getParameter("offset");

        if (isParameterProvided("limit", limit, resp) &&
                isParameterProvided("offset", offset, resp)) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(objectMapper.writeValueAsString(reportService.listReports(Integer.parseInt(limit), Integer.parseInt(offset))));
        } else {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(objectMapper.writeValueAsString(reportService.listReports()));
        }
    }

    private boolean isParameterProvided(String parameter, String value, HttpServletResponse response) throws IOException {
        if (value == null) {
            return false;
        }

        if (value != null && !NumberUtils.isInteger(value)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"message\": \"" + parameter + " was not a number\"}");
        }
        return true;
    }
}
