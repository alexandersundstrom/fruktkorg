package com.evry.fruktkorgrest.job;

import com.evry.fruktkorgservice.service.ReportService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FruktkorgReportJob extends QuartzJobBean {
    private ReportService reportService;
    private static final Logger logger = LogManager.getLogger(FruktkorgReportJob.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Removing read reports");
        reportService.removeReadReports();

        logger.info("Creating Fruktkorg Report...");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        File reportFile = new File("fruktkorg-report-" + LocalDateTime.now().format(dateTimeFormatter) + ".xml");

        reportService.createReport(reportFile.getAbsolutePath());

        logger.info("Fruktkorg Report Created");
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
}
