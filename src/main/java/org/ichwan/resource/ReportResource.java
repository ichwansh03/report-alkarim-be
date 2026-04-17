package org.ichwan.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.dto.request.ReportRequest;
import org.ichwan.service.ReportService;

@Path("/reports")
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
        return Response.status(Response.Status.CREATED).entity("report created").build();
    }

    @GET
    @Path("/regnumber/{regnumber}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getReportsByRegnumber(String regnumber) {
        return Response.ok(reportService.getReportsByRegnumber(regnumber)).build();
    }

    @GET
    @Path("/name/{name}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getReportsByUserName(String name) {
        return Response.ok(reportService.getReportsByUserName(name)).build();
    }

    @PUT
    @Path("/update/{id}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response updateReport(@PathParam("id") Long id, ReportRequest request) {
        reportService.update(request, id);
        return Response.ok("report updated").build();
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
