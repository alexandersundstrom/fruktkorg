package com.evry.fruktkorgservice.service;

import com.evry.fruktkorgpersistence.dao.ReportDAO;
import com.evry.fruktkorgpersistence.model.Fruktkorg;
import com.evry.fruktkorgpersistence.model.Report;
import com.evry.fruktkorgservice.exception.ReportMissingException;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.model.ImmutableReport;
import com.evry.fruktkorgservice.utils.ModelUtils;
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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReportServiceImpl implements ReportService {
    private ReportDAO reportDAO;
    private FruktkorgService fruktkorgService;
    private static Logger logger = LogManager.getLogger(ReportServiceImpl.class);

    public ReportServiceImpl(ReportDAO reportDAO, FruktkorgService fruktkorgService) {
        this.reportDAO = reportDAO;
        this.fruktkorgService = fruktkorgService;
    }

    @Override
    public List<ImmutableReport> listReports() {
        return reportDAO.listReports().stream().map(ModelUtils::convertReport).collect(Collectors.toList());
    }

    @Override
    public List<ImmutableReport> listReports(int limit, int offset) {
        return reportDAO.listReports(limit,offset).stream().map(ModelUtils::convertReport).collect(Collectors.toList());
    }

    @Override
    public ImmutableReport getAndMarkReport(long id) throws ReportMissingException {
        Report report = reportDAO.findReportById(id)
                .orElseThrow(() -> {
                    logger.warn("Unable to find report with id: " + id);
                    return new ReportMissingException("Unable to find report with id: " + id, id);
                });

        if (!report.isRead()) {
           report.setRead(true);
           report = reportDAO.merge(report);
        }

        return ModelUtils.convertReport(report);
    }

    @Override
    public ImmutableReport createReport(String path) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try {
            schema = schemaFactory.newSchema(new StreamSource(getClass().getClassLoader().getResourceAsStream("fruktkorg-report.xsd")));
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

        if(!reportFile.exists()) {
            return null;
        }

        Report report = new Report();
        report.setRead(false);
        report.setCreated(Instant.now());
        report.setLocation(path);

        reportDAO.persist(report);

        return ModelUtils.convertReport(report);
    }

    @Override
    public List<ImmutableFruktkorg> getFruktkorgarFromReport(long reportId) throws ReportMissingException {
        Report report = reportDAO.findReportById(reportId)
                .orElseThrow(() -> {
                    logger.warn("Unable to find report with id: " + reportId);
                    return new ReportMissingException("Unable to find report with id: " + reportId, reportId);
                });

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try {
            schema = schemaFactory.newSchema(new StreamSource(getClass().getClassLoader().getResourceAsStream("fruktkorg-report.xsd")));
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

        Fruktkorgar fruktkorgar = null;
        try {
            fruktkorgar = (Fruktkorgar)unmarshaller.unmarshal(new File(report.getLocation()));
        } catch (JAXBException e) {
            logger.error("Error unmachalling", e);
            return Collections.emptyList();
        }

        return fruktkorgar.fruktkorgList;
    }

    @Override
    public List<ImmutableFruktkorg> readFromByteArrayAndUpdateFruktkorgar(byte[] bytes) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try {
            schema = schemaFactory.newSchema(new StreamSource(getClass().getClassLoader().getResourceAsStream("fruktkorg-report.xsd")));
            JAXBContext jaxbContext = JAXBContext.newInstance(Fruktkorgar.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ReportValidationEventHandler eventHandler = new ReportValidationEventHandler();
            unmarshaller.setSchema(schema);
            unmarshaller.setEventHandler(eventHandler);


            Fruktkorgar fruktkorgar = (Fruktkorgar)unmarshaller.unmarshal(new ByteArrayInputStream(bytes));
            return fruktkorgar.fruktkorgList;

        } catch (Exception e) {
            logger.error("Error creating schema", e);
            return new ArrayList<>();
        }
    }
}
