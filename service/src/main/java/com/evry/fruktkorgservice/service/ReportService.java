package com.evry.fruktkorgservice.service;

import com.evry.fruktkorgservice.exception.ReportMissingException;
import com.evry.fruktkorgservice.model.ImmutableReport;

import java.util.List;

public interface ReportService {
    List<ImmutableReport> listReports();
    ImmutableReport getReportById(long id) throws ReportMissingException;
}
