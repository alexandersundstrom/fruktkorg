package com.evry.fruktkorgservice.service;

import com.evry.fruktkorgservice.exception.ReportMissingException;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.model.ImmutableReport;

import java.util.List;

public interface ReportService {
    List<ImmutableReport> listReports();
    List<ImmutableReport> listReports(int limit, int offset);
    ImmutableReport getAndMarkReport(long id) throws ReportMissingException;
    ImmutableReport createReport(String path);

    List<ImmutableFruktkorg> getFruktkorgarFromReport(long reportId) throws ReportMissingException;
}
