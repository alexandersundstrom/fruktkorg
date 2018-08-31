package com.evry.fruktkorgservice.utils;

import com.evry.fruktkorgservice.xml.FruktkorgarRestore;
import com.evry.fruktkorgservice.xml.FruktkorgarUpdate;
import com.evry.fruktkorgservice.xml.ReportValidationEventHandler;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;

public class XMLUtils {

    public static final String RESTORE_XSD = "fruktkorg-restore.xsd";
    public static final String UPDATE_XSD = "fruktkorg-update.xsd";
    public static final String REPORT_XSD = "fruktkorg-report.xsd";
    private static final Logger logger = LogManager.getLogger(XMLUtils.class);

    public static Unmarshaller getMarshaller(String schemaXSD) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema;

        try {
            schema = schemaFactory.newSchema(new StreamSource(XMLUtils.class.getClassLoader().getResourceAsStream(schemaXSD)));
        } catch (SAXException e) {
            logger.error("Error getting update xml schema", e);
            return null;
        }

        JAXBContext jaxbContext = null;
        try {
            switch (schemaXSD) {
                case UPDATE_XSD:
                    jaxbContext = JAXBContext.newInstance(FruktkorgarUpdate.class);
                    break;
                case RESTORE_XSD:
                    jaxbContext = JAXBContext.newInstance(FruktkorgarRestore.class);
                    break;
            }
        } catch (JAXBException e) {
            logger.error("Error creating context", e);
            return null;
        }

        Unmarshaller unmarshaller;
        try {
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            logger.error("Error creating unmashaller", e);
            return null;
        }

        ReportValidationEventHandler eventHandler = new ReportValidationEventHandler();
        unmarshaller.setSchema(schema);
        try {
            unmarshaller.setEventHandler(eventHandler);
        } catch (JAXBException e) {
            logger.error("Error setting event handler", e);
            return null;
        }
        return unmarshaller;
    }

    public static InputStream getReportXSD() {
        return XMLUtils.class.getClassLoader().getResourceAsStream(REPORT_XSD);
    }

    public static InputStream getUpdateXSD() {
        return XMLUtils.class.getClassLoader().getResourceAsStream(UPDATE_XSD);
    }

    public static InputStream getRestoreXSD() {
        return XMLUtils.class.getClassLoader().getResourceAsStream(RESTORE_XSD);
    }
}
