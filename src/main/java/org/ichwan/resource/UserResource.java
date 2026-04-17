package org.ichwan.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.dto.request.UserRequest;
import org.ichwan.dto.response.UserResponse;
import org.ichwan.service.UserService;

@Path("/api/v1/users")
@Consumes("application/json")
@Produces("application/json")
public class UserResource {

    @Inject
    private UserService userService;

    @PUT
    @Path("/update/{id}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response update(Long id, UserRequest req) {
        userService.update(req, id);
        return Response.ok("user updated").build();
    }

    @GET
    @Path("/class/{class}/roles/{roles}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getUsersByClassAndRoles(@PathParam("class") String clsroom, @PathParam("roles") String roles) {
        return Response.ok(userService.findByClsroomAndRoles(clsroom, roles)).build();
    }

    @GET
    @Path("/{regnumber}")
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
    @Path("/delete/{id}")
    @RolesAllowed("ADMINISTRATOR")
    public Response deleteUser(@PathParam("id") Long id) {
        userService.delete(id);
        return Response.ok("user deleted").build();
    }

    @GET
    @RolesAllowed({"TEACHER", "ADMINISTRATOR"})
    public Response getAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        return Response.ok(userService.getAll(page, size)).build();
    }

}
