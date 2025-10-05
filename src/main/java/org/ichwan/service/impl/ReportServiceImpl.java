package org.ichwan.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.ichwan.domain.Report;
import org.ichwan.dto.ReportRequest;
import org.ichwan.repository.ReportRepository;
import org.ichwan.service.ReportService;

import java.util.List;

@ApplicationScoped
public class ReportServiceImpl implements ReportService<Report, ReportRequest> {

    @Inject
    private ReportRepository repository;

    @Override
    public List<Report> getReportsByRegnumber(String regnumber) {
        return repository.findByUserRegnumber(regnumber);
    }

    @Override
    public List<Report> getReportsByUserName(String name) {
        return repository.findByUserName(name);
    }

    @Override
    public Report getReportById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Report createReport(ReportRequest request) {

        Report report = new Report();
        report.setCategory(request.category());
        report.setContent(request.content());
        report.setAction(request.action());

        return report;
    }

    @Override
    public Report updateReport(Report entity, Long id) {
        return null;
    }

    @Override
    public void deleteReport(Long id) {

    }
}
