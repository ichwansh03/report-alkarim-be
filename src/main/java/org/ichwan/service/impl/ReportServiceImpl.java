package org.ichwan.service.impl;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ichwan.domain.Report;
import org.ichwan.domain.User;
import org.ichwan.dto.ReportResponse;
import org.ichwan.exceptions.NotFoundException;
import org.ichwan.repository.ReportRepository;
import org.ichwan.repository.UserRepository;
import org.ichwan.service.ReportService;
import org.ichwan.util.MapperConfig;

import java.util.List;
import java.util.Optional;

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
        // Check if user exists first
        Optional.ofNullable(userRepository.findByRegnumber(regnumber))
                .orElseThrow(() -> new NotFoundException("User with registration number '" + regnumber + "' not found"));

        List<Report> reports = repository.findByUserRegnumber(regnumber);
        if (reports.isEmpty()) {
            throw new NotFoundException("No reports found for registration number '" + regnumber + "'");
        }

        return mapper.mapList(reports, ReportResponse.class);
    }

    @CacheResult(cacheName = "reports", lockTimeout = 3000)
    @Override
    public List<ReportResponse> getReportsByUserName(String name) {
        List<Report> reports = repository.findByUserName(name);
        if (reports.isEmpty()) {
            throw new NotFoundException("No reports found for user '" + name + "'");
        }

        return mapper.mapList(reports, ReportResponse.class);
    }

    @Override
    public Report getReportById(Long id) {
        return repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Report with id " + id + " not found"));
    }

    @Transactional
    @Override
    public void createReport(Report entity, Long userId) {
        User user = Optional.ofNullable(userRepository.findById(userId))
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        Report report = new Report();
        report.setAnswer(entity.getAnswer());
        report.setCategory(entity.getCategory());
        report.setContent(entity.getContent());
        report.setScore(entity.getScore());
        report.setUser(user);
        repository.persist(report); // was persisting entity instead of report
    }

    @Transactional
    @Override
    public void updateReport(Report entity, Long id) {
        Report report = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Report with id " + id + " not found"));

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
        repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Report with id " + id + " not found"));

        repository.deleteById(id);
    }
}
