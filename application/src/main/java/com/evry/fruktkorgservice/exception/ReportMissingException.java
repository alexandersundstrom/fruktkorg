package com.evry.fruktkorgservice.exception;

public class ReportMissingException extends Exception {
    private long reportId;

    public ReportMissingException(String message, long reportId) {
        super(message);
        this.reportId = reportId;
    }

    public long getReportId() {
        return reportId;
    }

    public void setReportId(long reportId) {
        this.reportId = reportId;
    }
}
