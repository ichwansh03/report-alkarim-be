package org.ichwan.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.dto.request.UserRequest;
import org.ichwan.dto.response.ApiResponse;
import org.ichwan.dto.response.UserResponse;
import org.ichwan.service.UserService;

import java.util.List;

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
        UserResponse response = userService.update(req, id);
        return Response.ok(ApiResponse.ok("Successfully updated data", response)).build();
    }

    @GET
    @Path("/class/{class}/roles/{roles}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getUsersByClassAndRoles(@PathParam("class") String clsroom, @PathParam("roles") String roles) {
        List<UserResponse> clsroomAndRoles = userService.findByClsroomAndRoles(clsroom, roles);
        return Response.ok(ApiResponse.ok("Successfully listed", clsroomAndRoles)).build();
    }

    @GET
    @Path("/{regnumber}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getUserByRegnumber(@PathParam("regnumber") String regnumber) {
        UserResponse user = userService.findByRegnumber(regnumber);

        return Response.ok(ApiResponse.ok("Data success for get", user)).build();
    }

    @GET
    @Path("/roles/{roles}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getUsersByRoles(@PathParam("roles") String roles) {
        List<UserResponse> byRoles = userService.findByRoles(roles);
        return Response.ok(ApiResponse.ok("Success", byRoles)).build();
    }

    @DELETE
    @Path("/delete/{id}")
    @RolesAllowed("ADMINISTRATOR")
    public Response deleteUser(@PathParam("id") Long id) {
        userService.delete(id);
        return Response.ok(ApiResponse.deleted("Successfully deleted")).build();
    }

    @GET
    @RolesAllowed({"TEACHER", "ADMINISTRATOR"})
    public Response getAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        return Response.ok(ApiResponse.ok("Success", userService.getAll(page, size))).build();
    }

}
