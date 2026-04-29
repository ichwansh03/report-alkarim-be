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
import org.ichwan.dto.request.UserRequest;
import org.ichwan.dto.response.ApiResponse;
import org.ichwan.dto.response.UserResponse;
import org.ichwan.service.UserService;

import java.util.List;

@Path("/api/v1/users")
@Consumes("application/json")
@Produces("application/json")
@Tag(name = "User", description = "Endpoints for managing users.")
public class UserResource {

    @Inject
    private UserService userService;

    @PUT
    @Path("/update/{id}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    @Operation(summary = "Update a user", description = "Updates an existing user by their ID. Only accessible by TEACHER, ADMINISTRATOR, and STUDENT roles.")
    @APIResponse(responseCode = "200", description = "User updated successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @APIResponse(responseCode = "404", description = "User not found")
    public Response update(@PathParam("id") Long id, UserRequest req) {
        UserResponse response = userService.update(req, id);
        return Response.ok(ApiResponse.ok("Successfully updated data", response)).build();
    }

    @GET
    @Path("/class/{class}/roles/{roles}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    @Operation(summary = "Get users by class and roles", description = "Returns a list of users filtered by classroom and role assignments.")
    @APIResponse(responseCode = "200", description = "Successfully retrieved users", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public Response getUsersByClassAndRoles(@PathParam("class") String clsroom, @PathParam("roles") String roles) {
        List<UserResponse> clsroomAndRoles = userService.findByClsroomAndRoles(clsroom, roles);
        return Response.ok(ApiResponse.ok("Successfully listed", clsroomAndRoles)).build();
    }

    @GET
    @Path("/{regnumber}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    @Operation(summary = "Get user by registration number", description = "Returns a specific user by their registration number.")
    @APIResponse(responseCode = "200", description = "Successfully retrieved user", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @APIResponse(responseCode = "404", description = "User not found")
    public Response getUserByRegnumber(@PathParam("regnumber") String regnumber) {
        UserResponse user = userService.findByRegnumber(regnumber);

        return Response.ok(ApiResponse.ok("Data success for get", user)).build();
    }

    @GET
    @Path("/roles/{roles}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    @Operation(summary = "Get users by roles", description = "Returns a list of users filtered by specific role.")
    @APIResponse(responseCode = "200", description = "Successfully retrieved users", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public Response getUsersByRoles(@PathParam("roles") String roles) {
        List<UserResponse> byRoles = userService.findByRoles(roles);
        return Response.ok(ApiResponse.ok("Success", byRoles)).build();
    }

    @DELETE
    @Path("/delete/{id}")
    @RolesAllowed("ADMINISTRATOR")
    @Operation(summary = "Delete a user", description = "Deletes an existing user by their ID. Only accessible by ADMINISTRATOR role.")
    @APIResponse(responseCode = "200", description = "User deleted successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @APIResponse(responseCode = "404", description = "User not found")
    public Response deleteUser(@PathParam("id") Long id) {
        userService.delete(id);
        return Response.ok(ApiResponse.deleted("Successfully deleted")).build();
    }

    @GET
    @RolesAllowed({"TEACHER", "ADMINISTRATOR"})
    @Operation(summary = "Get all users", description = "Returns a paginated list of all users. Only accessible by TEACHER and ADMINISTRATOR roles.")
    @APIResponse(responseCode = "200", description = "Successfully retrieved users", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public Response getAll(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        return Response.ok(ApiResponse.ok("Success", userService.getAll(page, size))).build();
    }

}
