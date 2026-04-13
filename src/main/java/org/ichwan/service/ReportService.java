package org.ichwan.service;

import org.ichwan.domain.Report;
import org.ichwan.dto.request.ReportRequest;
import org.ichwan.dto.response.ReportResponse;

import java.util.List;

public interface ReportService extends BaseService<ReportResponse> {

    List<ReportResponse> getReportsByRegnumber(String regnumber);

    List<ReportResponse> getReportsByUserName(String name);

    void createReport(ReportRequest entity);

    void updateReport(ReportRequest entity, Long id);

    void deleteReport(Long id);
}
