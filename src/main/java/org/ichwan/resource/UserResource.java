package org.ichwan.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.domain.User;
import org.ichwan.dto.AuthRequest;
import org.ichwan.dto.UserResponse;
import org.ichwan.service.impl.UserServiceImpl;

@Path("/user")
@Consumes("application/json")
@Produces("application/json")
public class UserResource {

    @Inject
    private UserServiceImpl userService;

    @PUT
    @Path("/update/{id}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response update(Long id, AuthRequest req) {
        User u = userService.findEntityById(id);
        if (u == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User tidak ditemukan").build();
        }
        u.setName(req.name());
        u.setClsroom(req.clsroom());
        u.setGender(req.gender());
        u.setRoles(req.roles());
        u.setPassword(req.password());
        userService.update(u, id);
        return Response.ok("user updated").build();
    }

    @GET
    @Path("/class/{class}/roles/{roles}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getUsersByClassAndRoles(@PathParam("class") String clsroom, @PathParam("roles") String roles) {
        return Response.ok(userService.findByClsroomAndRoles(clsroom, roles)).build();
    }

    @GET
    @Path("/user/{regnumber}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getUserByRegnumber(@PathParam("regnumber") String regnumber) {
        UserResponse user = userService.findByRegnumber(regnumber);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
        return Response.ok(user).build();
    }

    @GET
    @Path("/roles/{roles}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getUsersByRoles(@PathParam("roles") String roles) {
        return Response.ok(userService.findByRoles(roles)).build();
    }

    @DELETE
    @Path("/user/delete/{id}")
    @RolesAllowed("ADMINISTRATOR")
    public Response deleteUser(@PathParam("id") Long id) {
        userService.deleteUser(id);
        return Response.ok("user deleted").build();
    }
}
