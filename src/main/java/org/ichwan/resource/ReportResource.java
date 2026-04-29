package org.ichwan.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.ichwan.dto.request.ReportRequest;
import org.ichwan.dto.response.ApiResponse;
import org.ichwan.dto.response.ReportResponse;
import org.ichwan.service.ReportService;

@Path("/api/v1/reports")
@Consumes("application/json")
@Produces("application/json")
@Tag(name = "Report", description = "Endpoints for managing reports.")
public class ReportResource {

    @Inject
    private ReportService reportService;

    @POST
    @Path("/create")
    @RolesAllowed({"TEACHER","ADMINISTRATOR"})
    @Operation(summary = "Create a new report", description = "Creates a new report for students. Only accessible by TEACHER and ADMINISTRATOR roles.")
    @APIResponse(responseCode = "201", description = "Classroom created successfully")
    @APIResponse(responseCode = "409", description = "Classroom with the same name already exists")
    public Response createReport(ReportRequest request) {
        reportService.create(request);
        return Response.ok(ApiResponse.created("Successfully created")).build();
    }

    @GET
    @Path("/regnumber/{regnumber}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    @Operation(summary = "Get reports by registration number", description = "Returns a list of all reports for a specific student by their registration number.")
    @APIResponse(responseCode = "200", description = "Successfully retrieved reports", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public Response getReportsByRegnumber(@PathParam("regnumber") String regnumber) {
        return Response.ok(ApiResponse.ok("Success",reportService.getReportsByRegnumber(regnumber))).build();
    }

    @GET
    @Path("/name/{name}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    @Operation(summary = "Get reports by user name", description = "Returns a list of all reports for a specific student by their name.")
    @APIResponse(responseCode = "200", description = "Successfully retrieved reports", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public Response getReportsByUserName(@PathParam("name") String name) {
        return Response.ok(ApiResponse.ok("Success",reportService.getReportsByUserName(name))).build();
    }

    @PUT
    @Path("/update/{id}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    @Operation(summary = "Update a report", description = "Updates an existing report by its ID. Only accessible by TEACHER, ADMINISTRATOR, and STUDENT roles.")
    @APIResponse(responseCode = "200", description = "Report updated successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @APIResponse(responseCode = "404", description = "Report not found")
    public Response updateReport(@PathParam("id") Long id, ReportRequest request) {
        ReportResponse response = reportService.update(request, id);
        return Response.ok(ApiResponse.ok("Success", response)).build();
    }

    @DELETE
    @Path("/delete/{id}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR"})
    @Operation(summary = "Delete a report", description = "Deletes an existing report by its ID. Only accessible by TEACHER and ADMINISTRATOR roles.")
    @APIResponse(responseCode = "200", description = "Report deleted successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @APIResponse(responseCode = "404", description = "Report not found")
    public Response deleteReport(@PathParam("id") Long id) {
        reportService.delete(id);
        return Response.ok(ApiResponse.deleted("Success")).build();
    }

    @GET
    @RolesAllowed("ADMINISTRATOR")
    @Operation(summary = "Get all reports", description = "Returns a paginated list of all reports. Only accessible by ADMINISTRATOR role.")
    @APIResponse(responseCode = "200", description = "Successfully retrieved reports", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public Response getAllReports(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        return Response.ok(reportService.getAll(page, size)).build();
    }
}
