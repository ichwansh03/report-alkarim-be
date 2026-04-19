package org.ichwan.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.dto.request.ClassRoomRequest;
import org.ichwan.dto.response.ApiResponse;
import org.ichwan.dto.response.ClassRoomResponse;
import org.ichwan.service.ClassRoomService;

@Path("/api/v1/classes")
@Produces("application/json")
@Consumes("application/json")
public class ClassRoomResource {

    @Inject
    ClassRoomService classRoomService;

    @GET
    @Path("/teacher/{teacherName}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getClassRoomByTeacherName(@PathParam("teacherName") String teacherName) {
        return Response.ok(ApiResponse.ok("Success", classRoomService.getClassRoomByTeacherName(teacherName))).build();
    }

    @POST
    @Path("/create")
    @RolesAllowed("ADMINISTRATOR")
    public Response createClassRoom(ClassRoomRequest request) {
        ClassRoomResponse response = classRoomService.create(request);
        return Response.ok(ApiResponse.created("Success", response)).build();
    }

    @PUT
    @Path("/update/{id}")
    @RolesAllowed("ADMINISTRATOR")
    public Response updateClassRoom(@PathParam("id") Long id, ClassRoomRequest request) {
        ClassRoomResponse response = classRoomService.update(request, id);
        return Response.ok(ApiResponse.ok("Success",response)).build();
    }

    @DELETE
    @Path("/delete/{id}")
    @RolesAllowed("ADMINISTRATOR")
    public Response deleteClassRoom(@PathParam("id") Long id) {
        classRoomService.delete(id);
        return Response.ok(ApiResponse.deleted("Success")).build();
    }

    @GET
    @RolesAllowed("ADMINISTRATOR")
    public Response getAllClassRoom(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        return Response.ok(ApiResponse.ok("Success", classRoomService.getAll(page, size))).build();
    }
}
