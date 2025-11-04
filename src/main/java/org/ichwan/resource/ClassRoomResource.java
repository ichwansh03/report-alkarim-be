package org.ichwan.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.domain.ClassRoom;
import org.ichwan.dto.ClassRoomRequest;
import org.ichwan.service.impl.ClassRoomServiceImpl;

@Path("/class")
@Produces("application/json")
@Consumes("application/json")
public class ClassRoomResource {

    @Inject
    ClassRoomServiceImpl classRoomService;

    @GET
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getAllClassRooms() {
        return Response.ok(classRoomService.getAllClassRooms()).build();
    }

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
        ClassRoom classRoom = new ClassRoom();
        classRoom.setName(request.name());
        classRoom.setTeacherName(request.teacherId());
        classRoomService.createClassRoom(classRoom);
        return Response.status(Response.Status.CREATED).entity("ClassRoom created").build();
    }

    @PUT
    @Path("/update/{id}")
    @RolesAllowed("ADMINISTRATOR")
    public Response updateClassRoom(@PathParam("id") Long id, ClassRoomRequest request) {
        ClassRoom classRoom = new ClassRoom();
        classRoom.setName(request.name());
        classRoom.setTeacherName(request.teacherId());
        classRoomService.updateClassRoom(classRoom, id);
        return Response.ok("ClassRoom updated").build();
    }

    @DELETE
    @Path("/delete/{id}")
    @RolesAllowed("ADMINISTRATOR")
    public Response deleteClassRoom(@PathParam("id") Long id) {
        classRoomService.deleteClassRoom(id);
        return Response.ok("ClassRoom deleted").build();
    }
}
