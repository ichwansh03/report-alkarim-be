package org.ichwan.service.impl;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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

    @CacheResult(cacheName = "reports", lockTimeout = 3000)
    @Override
    public List<Report> getReportsByRegnumber(String regnumber) {
        return repository.findByUserRegnumber(regnumber);
    }

    @CacheResult(cacheName = "reports", lockTimeout = 3000)
    @Override
    public List<Report> getReportsByUserName(String name) {
        return repository.findByUserName(name);
    }

    @Override
    public Report getReportById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    @Override
    public void createReport(Report entity, String regnumber) {

        Report report = new Report();
        report.setAnswer(entity.getAnswer());
        report.setCategory(entity.getCategory());
        report.setContent(entity.getContent());
        report.setScore(entity.getScore());
        report.setUser(userRepository.findByRegnumber(regnumber));
        repository.persist(entity);
    }

    @Transactional
    @Override
    public void updateReport(Report entity, Long id) {
        Report report = repository.findById(id);
        if (report == null) {
            throw new IllegalArgumentException("report not found");
        }
        report.setAnswer(entity.getAnswer());
        report.setScore(entity.getScore());
        report.setContent(entity.getContent());
        report.setCategory(entity.getCategory());
        repository.persist(report);
    }

    @CacheInvalidate(cacheName = "reports")
    @Transactional
    @Override
    public void deleteReport(Long id) {
        repository.deleteById(id);
    }
}
