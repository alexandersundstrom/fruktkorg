package com.evry.fruktkorgrest.job;

import com.evry.fruktkorgrest.xml.Fruktkorgar;
import com.evry.fruktkorgrest.xml.ReportValidationEventHandler;
import com.evry.fruktkorgservice.service.FruktkorgService;
import com.evry.fruktkorgservice.service.ReportService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FruktkorgReportJob extends QuartzJobBean {
    private FruktkorgService fruktkorgService;
    private ReportService reportService;
    private static final Logger logger = LogManager.getLogger(FruktkorgReportJob.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Creating Fruktkorg Report");

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;
        try {
            schema = schemaFactory.newSchema(new File("rest/src/main/resources/fruktkorg-report.xsd"));
        } catch (SAXException e) {
            logger.error("Error creating schema", e);
            return;
        }

        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Fruktkorgar.class);
        } catch (JAXBException e) {
            logger.error("Error creating JAXB context", e);
            return;
        }

        Marshaller marshaller;
        try {
            marshaller = jaxbContext.createMarshaller();
        } catch (JAXBException e) {
            logger.error("Error creating Marshaller", e);
            return;
        }

        try {
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        } catch (PropertyException e) {
            logger.error("Error setting formatted output property", e);
            return;
        }

        ReportValidationEventHandler eventHandler = new ReportValidationEventHandler();
        marshaller.setSchema(schema);
        try {
            marshaller.setEventHandler(eventHandler);
        } catch (JAXBException e) {
            logger.error("Error setting event handler", e);
            return;
        }

        Fruktkorgar fruktkorgar = new Fruktkorgar();
        fruktkorgar.fruktkorgList = fruktkorgService.listFruktkorgar();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        File reportFile = new File("fruktkorg-report-" + LocalDateTime.now().format(dateTimeFormatter) + ".xml");
        try {
            marshaller.marshal(fruktkorgar, reportFile);
        } catch (JAXBException e) {
            logger.error("Error marshalling fruktkorgar", e);
            return;
        }

        if(!reportFile.exists()) {
            return;
        }

        reportService.createReport(reportFile.getAbsolutePath());
    }

    public void setFruktkorgService(FruktkorgService fruktkorgService) {
        this.fruktkorgService = fruktkorgService;
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
}
