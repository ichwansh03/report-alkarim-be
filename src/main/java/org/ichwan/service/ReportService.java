package org.ichwan.service;

import java.util.List;

public interface ReportService<E> {

    List<E> getReportsByRegnumber(String regnumber);

    List<E> getReportsByUserName(String name);

    E getReportById(Long id);

    void createReport(E entity, String regnumber);

    void updateReport(E entity, Long id);

    void deleteReport(Long id);
}
