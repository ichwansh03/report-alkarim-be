package org.ichwan.service.impl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.ichwan.domain.Report;
import org.ichwan.repository.ReportRepository;
import org.ichwan.repository.UserRepository;
import org.ichwan.service.ReportService;

import java.util.List;

@ApplicationScoped
public class ReportServiceImpl implements ReportService<Report> {

    @Inject
    private ReportRepository repository;
    @Inject
    UserRepository userRepository;

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
    public Report createReport(Report entity, String regnumber) {

        Report report = new Report();
        report.setAction(entity.getAction());
        report.setCategory(entity.getCategory());
        report.setContent(entity.getContent());
        report.setMarked(entity.getMarked());
        report.setUser(userRepository.findByRegnumber(regnumber));
        repository.persist(entity);
        return report;
    }

    @Override
    public Report updateReport(Report entity, Long id) {
        Report report = repository.findById(id);
        if (report == null) {
            throw new IllegalArgumentException("report not found");
        }
        report.setAction(entity.getAction());
        report.setMarked(entity.getMarked());
        report.setContent(entity.getContent());
        report.setCategory(entity.getCategory());
        repository.persist(report);
        return report;
    }

    @Override
    public void deleteReport(Long id) {
        repository.deleteById(id);
    }
}
