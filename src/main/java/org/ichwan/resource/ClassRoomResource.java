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
import org.ichwan.dto.request.ClassRoomRequest;
import org.ichwan.dto.response.ApiResponse;
import org.ichwan.dto.response.ClassRoomResponse;
import org.ichwan.service.ClassRoomService;

@Path("/api/v1/classes")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Class Room", description = "Endpoints for managing class rooms.")
public class ClassRoomResource {

    @Inject
    ClassRoomService classRoomService;

    @GET
    @Path("/teacher/{teacherName}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    @Operation(summary = "Get all classroom", description = "Returns a list of all classroom based on teacher.")
    @APIResponse(responseCode = "200", description = "Successfully retrieved classrooms", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public Response getClassRoomByTeacherName(@PathParam("teacherName") String teacherName) {
        return Response.ok(ApiResponse.ok("Success", classRoomService.getClassRoomByTeacherName(teacherName))).build();
    }

    @POST
    @Path("/create")
    @RolesAllowed("ADMINISTRATOR")
    @Operation(summary = "Create a new class room", description = "Creates a new class room for students. Only accessible by TEACHER and ADMINISTRATOR roles.")
    @APIResponse(responseCode = "201", description = "Classroom created successfully")
    @APIResponse(responseCode = "409", description = "Classroom with the same name already exists")
    public Response createClassRoom(ClassRoomRequest request) {
        ClassRoomResponse response = classRoomService.create(request);
        return Response.ok(ApiResponse.created("Success", response)).build();
    }

    @PUT
    @Path("/update/{id}")
    @RolesAllowed("ADMINISTRATOR")
    @Operation(summary = "Update a classroom", description = "Updates an existing classroom by its ID. Only accessible by ADMINISTRATOR role.")
    @APIResponse(responseCode = "200", description = "Classroom updated successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @APIResponse(responseCode = "404", description = "Classroom not found")
    public Response updateClassRoom(@PathParam("id") Long id, ClassRoomRequest request) {
        ClassRoomResponse response = classRoomService.update(request, id);
        return Response.ok(ApiResponse.ok("Success",response)).build();
    }

    @DELETE
    @Path("/delete/{id}")
    @RolesAllowed("ADMINISTRATOR")
    @Operation(summary = "Delete a classroom", description = "Deletes an existing classroom by its ID. Only accessible by TEACHER and ADMINISTRATOR roles.")
    @APIResponse(responseCode = "200", description = "Classroom deleted successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @APIResponse(responseCode = "404", description = "Classroom not found")
    public Response deleteClassRoom(@PathParam("id") Long id) {
        classRoomService.delete(id);
        return Response.ok(ApiResponse.deleted("Success")).build();
    }

    @GET
    @RolesAllowed("ADMINISTRATOR")
    @Operation(summary = "Get all classroom", description = "Returns a list of all classroom.")
    @APIResponse(responseCode = "200", description = "Successfully retrieved classrooms", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public Response getAllClassRoom(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        return Response.ok(ApiResponse.ok("Success", classRoomService.getAll(page, size))).build();
    }
}
