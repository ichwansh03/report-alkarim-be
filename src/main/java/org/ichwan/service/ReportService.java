package org.ichwan.service;

import java.util.List;

public interface ReportService<E, R> {

    List<R> getReportsByRegnumber(String regnumber);

    List<R> getReportsByUserName(String name);

    E getReportById(Long id);

    void createReport(E entity, Long userId);

    void updateReport(E entity, Long id);

    void deleteReport(Long id);
}
