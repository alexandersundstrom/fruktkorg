package com.evry.fruktkorgservice.service;

import com.evry.fruktkorgpersistence.dao.ReportDAO;
import com.evry.fruktkorgservice.exception.ReportMissingException;
import com.evry.fruktkorgservice.model.ImmutableReport;
import com.evry.fruktkorgservice.utils.ModelUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class ReportServiceImpl implements ReportService {
    private ReportDAO reportDAO;
    private static Logger logger = LogManager.getLogger(ReportServiceImpl.class);

    public ReportServiceImpl(ReportDAO reportDAO) {
        this.reportDAO = reportDAO;
    }

    @Override
    public List<ImmutableReport> listReports() {
        return reportDAO.listReports().stream().map(ModelUtils::convertReport).collect(Collectors.toList());
    }

    @Override
    public ImmutableReport getReportById(long id) throws ReportMissingException {
        return reportDAO.findReportById(id)
                .map(ModelUtils::convertReport)
                .orElseThrow(() -> {
            logger.warn("Unable to find report with id: " + id);
            return new ReportMissingException("Unable to find report with id: " + id, id);
        });
    }
}
