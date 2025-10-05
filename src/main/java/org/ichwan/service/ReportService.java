package org.ichwan.service;

import java.util.List;

public interface ReportService<E, R> {

    List<E> getReportsByRegnumber(String regnumber);

    List<E> getReportsByUserName(String name);

    E getReportById(Long id);

    E createReport(R request);

    E updateReport(E entity, Long id);

    void deleteReport(Long id);
}
