package org.ichwan.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.domain.Report;
import org.ichwan.dto.ReportRequest;
import org.ichwan.service.impl.ReportServiceImpl;
import org.ichwan.service.impl.UserServiceImpl;

@Path("/reports")
public class ReportResource {

    @Inject
    private ReportServiceImpl reportService;
    @Inject
    private UserServiceImpl userService;

    @POST
    @Path("/create")
    @Consumes("application/json")
    public Response createReport(ReportRequest request) {
        Report report = new Report();
        report.setCategory(request.category());
        report.setContent(request.content());
        report.setMarked(request.marked());
        report.setUser(userService.findByRegnumber(request.regnumber()));
        report.setAction(request.action() != null ? request.action() : false);
        reportService.createReport(report, request.regnumber());
        return Response.status(Response.Status.CREATED).entity("report created").build();
    }

    @GET
    @Path("/report/{regnumber}")
    public Response getReportsByRegnumber(String regnumber) {
        return Response.ok(reportService.getReportsByRegnumber(regnumber)).build();
    }

    @GET
    @Path("/report/{name}")
    public Response getReportsByUserName(String name) {
        return Response.ok(reportService.getReportsByUserName(name)).build();
    }

    @POST
    @Path("/update/{id}")
    @Consumes("application/json")
    public Response updateReport(@PathParam("id") Long id, ReportRequest request) {
        Report report = reportService.getReportById(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("report not found").build();
        }
        report.setCategory(request.category());
        report.setContent(request.content());
        report.setMarked(request.marked());
        report.setAction(request.action() != null ? request.action() : report.getAction());
        reportService.updateReport(report, id);
        return Response.ok("report updated").build();
    }

    @POST
    @Path("/delete")
    @Consumes("application/json")
    public Response deleteReport(Long id) {
        Report report = reportService.getReportById(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("report not found").build();
        }
        reportService.deleteReport(id);
        return Response.ok("report deleted").build();
    }
}
