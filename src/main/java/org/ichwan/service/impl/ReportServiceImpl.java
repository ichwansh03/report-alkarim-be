package org.ichwan.service.impl;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.ichwan.domain.Report;
import org.ichwan.domain.User;
import org.ichwan.dto.request.ReportRequest;
import org.ichwan.dto.response.PageResponse;
import org.ichwan.dto.response.ReportResponse;
import org.ichwan.exceptions.NotFoundException;
import org.ichwan.repository.CategoryRepository;
import org.ichwan.repository.ReportRepository;
import org.ichwan.repository.UserRepository;
import org.ichwan.service.ReportService;
import org.ichwan.util.MapperConfig;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ReportServiceImpl implements ReportService {

    @Inject
    private ReportRepository repository;
    @Inject
    private CategoryRepository categoryRepository;
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

    @Transactional
    @Override
    public void createReport(ReportRequest entity) {
        User user = Optional.ofNullable(userRepository.findById(entity.userId()))
                .orElseThrow(() -> new NotFoundException("User with id " + entity.userId() + " not found"));

        Report report = new Report();
        report.setAnswer(entity.answer());
        report.setCategory(categoryRepository.findById(entity.categoryId()));
        report.setContent(entity.content());
        report.setScore(entity.score());
        report.setUser(user);
        repository.persist(report); // was persisting entity instead of report
    }

    @Transactional
    @Override
    public void updateReport(ReportRequest entity, Long id) {
        Report report = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Report with id " + id + " not found"));

        report.setAnswer(entity.answer());
        report.setCategory(categoryRepository.findById(entity.categoryId()));
        report.setContent(entity.content());
        report.setScore(entity.score());
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

    @Override
    public ReportResponse findById(Long id) {
        Report report = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Report with id " + id + " not found"));
        return mapper.map(report, ReportResponse.class);
    }

    @Override
    public PageResponse<ReportResponse> getAll(int page, int size) {
        PanacheQuery<Report> query = repository.findAll();
        query.page(Page.of(page, size));

        List<Report> reports = query.list();
        if (reports.isEmpty()) {
            throw new NotFoundException("No users found");
        }

        long totalItems = query.count();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        List<ReportResponse> data = mapper.mapList(reports, ReportResponse.class);

        return new PageResponse<>(data, page, size, totalItems, totalPages);
    }
}
