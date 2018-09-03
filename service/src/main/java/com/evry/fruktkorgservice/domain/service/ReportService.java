package com.evry.fruktkorgservice.domain.service;

import com.evry.fruktkorgpersistence.dao.ReportRepositoryHibernate;
import com.evry.fruktkorgpersistence.model.Report;
import com.evry.fruktkorgservice.domain.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.domain.model.ImmutableReport;
import com.evry.fruktkorgservice.exception.ReportMissingException;
import com.evry.fruktkorgservice.utils.ModelUtils;
import com.evry.fruktkorgservice.utils.XMLUtils;
import com.evry.fruktkorgservice.xml.Fruktkorgar;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.xml.bind.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService {
    private ReportRepositoryHibernate reportRepository;
    private FruktkorgService fruktkorgService;
    private static Logger logger = LogManager.getLogger(ReportService.class);

    public ReportService(ReportRepositoryHibernate reportRepositoryHibernate, FruktkorgService fruktkorgService) {
        this.reportRepository = reportRepositoryHibernate;
        this.fruktkorgService = fruktkorgService;
    }

    public List<ImmutableReport> listReports() {
        return reportRepository.findAll().stream().map(ModelUtils::convertReport).collect(Collectors.toList());
    }

    public List<ImmutableReport> listReports(int limit, int offset) {
        return reportRepository.findAllByLimitAndOffset(limit, offset).stream().map(ModelUtils::convertReport).collect(Collectors.toList());
    }

    public InputStream getAndMarkReport(long id) throws ReportMissingException, FileNotFoundException {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Unable to find report with id: " + id);
                    return new ReportMissingException("Unable to find report with id: " + id, id);
                });

        if (!report.isRead()) {
            report.setRead(true);
            report = reportRepository.merge(report);
        }

        return XMLUtils.getReport(report);
    }

    public ImmutableReport createReport(String path) {
        Marshaller marshaller = XMLUtils.getFruktkorgarMarshaller();

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

        reportRepository.persist(report);

        return ModelUtils.convertReport(report);
    }

    public void removeReport(long reportId) throws ReportMissingException {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> {
                    logger.warn("Unable to find report with id: " + reportId);
                    return new ReportMissingException("Unable to find report with id: " + reportId, reportId);
                });

        File reportFile = new File(report.getLocation());
        if (reportFile.exists()) {
            reportFile.delete();
        }

        reportRepository.remove(report);
    }

    public void removeReadReports() {
        List<Report> readReports = reportRepository.getAllByRead();

        if (readReports.isEmpty()) {
            return;
        }

        for (Report report : readReports) {
            File reportFile = new File(report.getLocation());
            if (reportFile.exists()) {
                reportFile.delete();
            }
        }

        reportRepository.removeByRead();
    }

    public List<ImmutableFruktkorg> getFruktkorgarFromReport(long reportId) throws ReportMissingException {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> {
                    logger.warn("Unable to find report with id: " + reportId);
                    return new ReportMissingException("Unable to find report with id: " + reportId, reportId);
                });

       Unmarshaller unmarshaller = XMLUtils.getUnmarshaller(XMLUtils.REPORT_XSD);

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
