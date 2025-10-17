package org.ichwan.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.domain.Report;
import org.ichwan.dto.ReportRequest;
import org.ichwan.service.impl.ReportServiceImpl;
import org.ichwan.service.impl.UserServiceImpl;

@Path("/reports")
@Consumes("application/json")
@Produces("application/json")
public class ReportResource {

    @Inject
    private ReportServiceImpl reportService;
    @Inject
    private UserServiceImpl userService;

    @POST
    @Path("/create")
    public Response createReport(ReportRequest request) {
        Report report = new Report();
        report.setCategory(request.category());
        report.setContent(request.content());
        report.setAnswer(request.answer());
        report.setUser(userService.findByRegnumber(request.regnumber()));
        report.setScore(request.score());
        reportService.createReport(report, request.regnumber());
        return Response.status(Response.Status.CREATED).entity("report created").build();
    }

    @GET
    @Path("/regnumber/{regnumber}")
    public Response getReportsByRegnumber(String regnumber) {
        return Response.ok(reportService.getReportsByRegnumber(regnumber)).build();
    }

    @GET
    @Path("/name/{name}")
    public Response getReportsByUserName(String name) {
        return Response.ok(reportService.getReportsByUserName(name)).build();
    }

    @PUT
    @Path("/update/{id}")
    public Response updateReport(@PathParam("id") Long id, ReportRequest request) {
        Report report = reportService.getReportById(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("report not found").build();
        }
        report.setCategory(request.category());
        report.setContent(request.content());
        report.setScore(request.score());
        report.setAnswer(request.answer());
        reportService.updateReport(report, id);
        return Response.ok("report updated").build();
    }

    @DELETE
    @Path("/delete/{id}")
    public Response deleteReport(@PathParam("id") Long id) {
        Report report = reportService.getReportById(id);
        if (report == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("report not found").build();
        }
        reportService.deleteReport(id);
        return Response.ok("report deleted").build();
    }
}
