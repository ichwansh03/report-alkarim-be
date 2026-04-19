package org.ichwan.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.dto.request.ReportRequest;
import org.ichwan.dto.response.ApiResponse;
import org.ichwan.service.ReportService;

@Path("/api/v1/reports")
@Consumes("application/json")
@Produces("application/json")
public class ReportResource {

    @Inject
    private ReportService reportService;

    @POST
    @Path("/create")
    @RolesAllowed({"TEACHER","ADMINISTRATOR"})
    public Response createReport(ReportRequest request) {
        reportService.create(request);
        return Response.ok(ApiResponse.created("Successfully created")).build();
    }

    @GET
    @Path("/regnumber/{regnumber}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getReportsByRegnumber(String regnumber) {
        return Response.ok(ApiResponse.ok("Success",reportService.getReportsByRegnumber(regnumber))).build();
    }

    @GET
    @Path("/name/{name}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getReportsByUserName(String name) {
        return Response.ok(ApiResponse.ok("Success",reportService.getReportsByUserName(name))).build();
    }

    @PUT
    @Path("/update/{id}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response updateReport(@PathParam("id") Long id, ReportRequest request) {
        reportService.update(request, id);
        return Response.ok(ApiResponse.ok("Success")).build();
    }

    @DELETE
    @Path("/delete/{id}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR"})
    public Response deleteReport(@PathParam("id") Long id) {
        reportService.delete(id);
        return Response.ok("report deleted").build();
    }

    @GET
    @RolesAllowed("ADMINISTRATOR")
    public Response getAllReports(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        return Response.ok(reportService.getAll(page, size)).build();
    }
}
