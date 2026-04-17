package org.ichwan.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.dto.request.ClassRoomRequest;
import org.ichwan.service.ClassRoomService;

@Path("/class")
@Produces("application/json")
@Consumes("application/json")
public class ClassRoomResource {

    @Inject
    ClassRoomService classRoomService;

    @GET
    @Path("/teacher/{teacherName}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getClassRoomByTeacherName(@PathParam("teacherName") String teacherName) {
        return Response.ok(classRoomService.getClassRoomByTeacherName(teacherName)).build();
    }

    @POST
    @Path("/create")
    @RolesAllowed("ADMINISTRATOR")
    public Response createClassRoom(ClassRoomRequest request) {
        classRoomService.create(request);
        return Response.status(Response.Status.CREATED).entity("ClassRoom created").build();
    }

    @PUT
    @Path("/update/{id}")
    @RolesAllowed("ADMINISTRATOR")
    public Response updateClassRoom(@PathParam("id") Long id, ClassRoomRequest request) {
        classRoomService.update(request, id);
        return Response.ok("ClassRoom updated").build();
    }

    @DELETE
    @Path("/delete/{id}")
    @RolesAllowed("ADMINISTRATOR")
    public Response deleteClassRoom(@PathParam("id") Long id) {
        classRoomService.delete(id);
        return Response.ok("ClassRoom deleted").build();
    }

    @GET
    @RolesAllowed("ADMINISTRATOR")
    public Response getAllClassRoom(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        return Response.ok(classRoomService.getAll(page, size)).build();
    }
}
