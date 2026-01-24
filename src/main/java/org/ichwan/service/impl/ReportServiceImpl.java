package org.ichwan.service.impl;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ichwan.domain.Report;
import org.ichwan.dto.ReportResponse;
import org.ichwan.repository.ReportRepository;
import org.ichwan.repository.UserRepository;
import org.ichwan.service.ReportService;
import org.ichwan.util.MapperConfig;

import java.util.List;

@ApplicationScoped
public class ReportServiceImpl implements ReportService<Report, ReportResponse> {

    @Inject
    private ReportRepository repository;
    @Inject
    UserRepository userRepository;
    @Inject
    private MapperConfig mapper;

    @CacheResult(cacheName = "reports", lockTimeout = 3000)
    @Override
    public List<ReportResponse> getReportsByRegnumber(String regnumber) {
        List<Report> byUserRegnumber = repository.findByUserRegnumber(regnumber);

        return mapper.mapList(byUserRegnumber, ReportResponse.class);
    }

    @CacheResult(cacheName = "reports", lockTimeout = 3000)
    @Override
    public List<ReportResponse> getReportsByUserName(String name) {
        List<Report> byUserName = repository.findByUserName(name);
        return mapper.mapList(byUserName, ReportResponse.class);
    }

    @Override
    public Report getReportById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    @Override
    public void createReport(Report entity, Long userId) {

        Report report = new Report();
        report.setAnswer(entity.getAnswer());
        report.setCategory(entity.getCategory());
        report.setContent(entity.getContent());
        report.setScore(entity.getScore());
        report.setUser(userRepository.findById(userId));
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
