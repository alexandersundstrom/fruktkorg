package com.evry.fruktkorgservice.domain.service;

import com.evry.fruktkorgpersistence.dao.ReportDAO;
import com.evry.fruktkorgpersistence.model.Report;
import com.evry.fruktkorgservice.domain.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.domain.model.ImmutableReport;
import com.evry.fruktkorgservice.exception.ReportMissingException;
import com.evry.fruktkorgservice.utils.ModelUtils;
import com.evry.fruktkorgservice.utils.XMLUtils;
import com.evry.fruktkorgservice.xml.Fruktkorgar;
import com.evry.fruktkorgservice.xml.ReportValidationEventHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService {
    private ReportDAO reportDAO;
    private FruktkorgService fruktkorgService;
    private static Logger logger = LogManager.getLogger(ReportService.class);

    public ReportService(ReportDAO reportDAO, FruktkorgService fruktkorgService) {
        this.reportDAO = reportDAO;
        this.fruktkorgService = fruktkorgService;
    }

    public List<ImmutableReport> listReports() {
        return reportDAO.findAll().stream().map(ModelUtils::convertReport).collect(Collectors.toList());
    }

    public List<ImmutableReport> listReports(int limit, int offset) {
        return reportDAO.findAllByLimitAndOffset(limit, offset).stream().map(ModelUtils::convertReport).collect(Collectors.toList());
    }

    public InputStream getAndMarkReport(long id) throws ReportMissingException, FileNotFoundException {
        Report report = reportDAO.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Unable to find report with id: " + id);
                    return new ReportMissingException("Unable to find report with id: " + id, id);
                });

        if (!report.isRead()) {
            report.setRead(true);
            report = reportDAO.merge(report);
        }

        return XMLUtils.getReport(report);
    }

    public ImmutableReport createReport(String path) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try {
            schema = schemaFactory.newSchema(new StreamSource(XMLUtils.getReportXSD()));
        } catch (SAXException e) {
            logger.error("Error creating schema", e);
            return null;
        }

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Fruktkorgar.class);
        } catch (JAXBException e) {
            logger.error("Error creating JAXB context", e);
            return null;
        }

        Marshaller marshaller;
        try {
            marshaller = jaxbContext.createMarshaller();
        } catch (JAXBException e) {
            logger.error("Error creating Marshaller", e);
            return null;
        }

        try {
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (PropertyException e) {
            logger.error("Error setting formatted output property", e);
            return null;
        }

        ReportValidationEventHandler eventHandler = new ReportValidationEventHandler();
        marshaller.setSchema(schema);
        try {
            marshaller.setEventHandler(eventHandler);
        } catch (JAXBException e) {
            logger.error("Error setting event handler", e);
            return null;
        }

        Fruktkorgar fruktkorgar = new Fruktkorgar();
        fruktkorgar.fruktkorgList = fruktkorgService.listFruktkorgar();

        File reportFile = new File(path);
        try {
            marshaller.marshal(fruktkorgar, reportFile);
        } catch (JAXBException e) {
            logger.error("Error marshalling fruktkorgar", e);
            return null;
        }

        if (!reportFile.exists()) {
            return null;
        }

        Report report = new Report();
        report.setRead(false);
        report.setCreated(Instant.now());
        report.setLocation(path);

        reportDAO.persist(report);

        return ModelUtils.convertReport(report);
    }

    public void removeReport(long reportId) throws ReportMissingException {
        Report report = reportDAO.findById(reportId)
                .orElseThrow(() -> {
                    logger.warn("Unable to find report with id: " + reportId);
                    return new ReportMissingException("Unable to find report with id: " + reportId, reportId);
                });

        File reportFile = new File(report.getLocation());
        if (reportFile.exists()) {
            reportFile.delete();
        }

        reportDAO.remove(report);
    }

    public void removeReadReports() {
        List<Report> readReports = reportDAO.getAllByRead();

        if (readReports.isEmpty()) {
            return;
        }

        for (Report report : readReports) {
            File reportFile = new File(report.getLocation());
            if (reportFile.exists()) {
                reportFile.delete();
            }
        }

        reportDAO.removeByRead();
    }

    public List<ImmutableFruktkorg> getFruktkorgarFromReport(long reportId) throws ReportMissingException {
        Report report = reportDAO.findById(reportId)
                .orElseThrow(() -> {
                    logger.warn("Unable to find report with id: " + reportId);
                    return new ReportMissingException("Unable to find report with id: " + reportId, reportId);
                });

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try {
            schema = schemaFactory.newSchema(new StreamSource(XMLUtils.getReportXSD()));
        } catch (SAXException e) {
            logger.error("Error creating schema", e);
            return Collections.emptyList();
        }

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Fruktkorgar.class);
        } catch (JAXBException e) {
            logger.error("Error creating JAXB context", e);
            return Collections.emptyList();
        }

        Unmarshaller unmarshaller;
        try {
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            logger.error("Error creating Marshaller", e);
            return Collections.emptyList();
        }

        ReportValidationEventHandler eventHandler = new ReportValidationEventHandler();
        unmarshaller.setSchema(schema);
        try {
            unmarshaller.setEventHandler(eventHandler);
        } catch (JAXBException e) {
            logger.error("Error setting event handler", e);
            return Collections.emptyList();
        }

        Fruktkorgar fruktkorgar;
        try {
            fruktkorgar = (Fruktkorgar) unmarshaller.unmarshal(new File(report.getLocation()));
        } catch (JAXBException e) {
            logger.error("Error unmachalling", e);
            return Collections.emptyList();
        }

        return fruktkorgar.fruktkorgList;
    }

    public InputStream getUpdateXSD() {
        return XMLUtils.getUpdateXSD();
    }

    public InputStream getRestoreXSD() {
        return XMLUtils.getRestoreXSD();
    }
}