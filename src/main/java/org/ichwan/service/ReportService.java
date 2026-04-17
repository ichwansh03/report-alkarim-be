package org.ichwan.service;

import org.ichwan.dto.request.ReportRequest;
import org.ichwan.dto.response.ReportResponse;

import java.util.List;

public interface ReportService extends BaseService<ReportRequest, ReportResponse> {

    List<ReportResponse> getReportsByRegnumber(String regnumber);

    List<ReportResponse> getReportsByUserName(String name);

}
