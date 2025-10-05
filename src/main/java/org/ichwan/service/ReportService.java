package org.ichwan.service;

import java.util.List;

public interface ReportService<E> {

    List<E> getReportsByRegnumber(String regnumber);

    List<E> getReportsByUserName(String name);

    E getReportById(Long id);

    E createReport(E entity, String regnumber);

    E updateReport(E entity, Long id);

    void deleteReport(Long id);
}
