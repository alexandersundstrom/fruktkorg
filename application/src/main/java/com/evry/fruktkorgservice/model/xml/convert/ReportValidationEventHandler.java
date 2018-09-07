package com.evry.fruktkorgservice.model.xml.convert;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.SAXParseException;

import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

public class ReportValidationEventHandler implements ValidationEventHandler {
    private static Logger logger = LogManager.getLogger(ReportValidationEventHandler.class);
    
    @Override
    public boolean handleEvent(ValidationEvent event) {
        logger.info("\nEVENT");
        logger.info("SEVERITY:  " + event.getSeverity());
        logger.info("MESSAGE:  " + event.getMessage());
        logger.info("LINKED EXCEPTION:  " + event.getLinkedException());
        logger.info("LOCATOR");
        logger.info("    LINE NUMBER:  " + event.getLocator().getLineNumber());
        logger.info("    COLUMN NUMBER:  " + event.getLocator().getColumnNumber());
        logger.info("    OFFSET:  " + event.getLocator().getOffset());
        logger.info("    OBJECT:  " + event.getLocator().getObject());
        logger.info("    NODE:  " + event.getLocator().getNode());
        logger.info("    URL:  " + event.getLocator().getURL());
        if (event.getLinkedException() instanceof SAXParseException) {
            return false;
        }
        return true;
    }
}
