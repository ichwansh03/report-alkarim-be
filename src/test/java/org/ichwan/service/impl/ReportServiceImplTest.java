package org.ichwan.service.impl;

import org.ichwan.domain.Report;
import org.ichwan.repository.ReportRepository;
import org.ichwan.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceImplTest {
    @Mock
    ReportRepository reportRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    ReportServiceImpl reportService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /*@Test
    void testGetReportsByRegnumber() {
        Report report = new Report();
        when(reportRepository.findByUserRegnumber("123")).thenReturn(Arrays.asList(report));
        List<Report> result = reportService.getReportsByRegnumber("123");
        assertEquals(1, result.size());
    }*/

    /*@Test
    void testGetReportById() {
        Report report = new Report();
        when(reportRepository.findById(1L)).thenReturn(report);
        Report result = reportService.getReportById(1L);
        assertNotNull(result);
    }*/

    @Test
    void testCreateReport() {
        Report report = new Report();
        when(userRepository.findByRegnumber("123")).thenReturn(null);
        reportService.createReport(report, 123L);
        verify(reportRepository, times(1)).persist(report);
    }

    @Test
    void testUpdateReport() {
        Report report = new Report();
        when(reportRepository.findById(1L)).thenReturn(report);
        reportService.updateReport(report, 1L);
        verify(reportRepository, times(1)).persist(report);
    }

    @Test
    void testDeleteReport() {
        reportService.deleteReport(1L);
        verify(reportRepository, times(1)).deleteById(1L);
    }
}

