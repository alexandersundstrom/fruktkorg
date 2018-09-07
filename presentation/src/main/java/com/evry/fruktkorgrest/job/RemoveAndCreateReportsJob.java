package com.evry.fruktkorgrest.job;

import com.evry.fruktkorgservice.ReportService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RemoveAndCreateReportsJob extends QuartzJobBean {
    private ReportService reportService;
    private static final Logger logger = LogManager.getLogger(RemoveAndCreateReportsJob.class);
    private final String REPORTS_DIRECTORY = "generated-reports/";

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        logger.info("Removing read reports");
        reportService.removeReadReports();

        logger.info("Creating Fruktkorg Report...");

        File reportsDirectory = new File("reports");
        if (!reportsDirectory.exists()) {
            logger.info("Creating reports directory");
            reportsDirectory.mkdir();
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        File reportFile = new File(REPORTS_DIRECTORY + "fruktkorg-report-" + LocalDateTime.now().format(dateTimeFormatter) + ".xmlconversion");

        reportService.createReport(reportFile.getAbsolutePath());

        logger.info("Fruktkorg Report Created");
    }

    public void setReportService(ReportService reportService) {
        this.reportService = reportService;
    }
}
