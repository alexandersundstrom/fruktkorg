package com.evry.fruktkorgservice.service;

import com.evry.fruktkorgservice.exception.ReportMissingException;
import com.evry.fruktkorgservice.model.ImmutableFruktkorg;
import com.evry.fruktkorgservice.model.ImmutableReport;

import java.io.InputStream;
import java.util.List;

public interface ReportService {
    List<ImmutableReport> listReports();
    List<ImmutableReport> listReports(int limit, int offset);
    ImmutableReport getAndMarkReport(long id) throws ReportMissingException;
    ImmutableReport createReport(String path);
    void removeReport(long reportId) throws ReportMissingException;
    void removeReadReports();
    List<ImmutableFruktkorg> getFruktkorgarFromReport(long reportId) throws ReportMissingException;
    InputStream getUpdateXSD();
    InputStream getRestoreXSD();
}
