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
import org.ichwan.dto.request.CategoryRequest;
import org.ichwan.dto.response.ApiResponse;
import org.ichwan.service.impl.CategoryServiceImpl;

@Path("api/v1/categories")
@Produces("application/json")
@Consumes("application/json")
@Tag(name = "Category", description = "Endpoints for managing question and report categories.")
public class CategoryResource {

    @Inject
    private CategoryServiceImpl service;

    @GET
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    @Operation(summary = "Get all categories", description = "Returns a list of all available categories.")
    @APIResponse(responseCode = "200", description = "Successfully retrieved categories", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public Response getAllCategories() {
        return Response.ok(ApiResponse.ok("Success", service.getAllCategories())).build();
    }

    // Endpoint to create a new category
     @POST
     @Path("/create")
     @RolesAllowed({"TEACHER","ADMINISTRATOR"})
     @Operation(summary = "Create a new category", description = "Creates a new category for questions and reports. Only accessible by TEACHER and ADMINISTRATOR roles.")
     @APIResponse(responseCode = "201", description = "Category created successfully")
     @APIResponse(responseCode = "409", description = "Category with the same name already exists")
     public Response createCategory(CategoryRequest request) {
         service.createCategory(request.name());
         return Response.status(Response.Status.CREATED).entity("Category created").build();
     }

    // Endpoint to delete a category by ID
    @DELETE
    @Path("/delete/{id}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR"})
    @Operation(summary = "Delete a category", description = "Deletes an existing category by its ID. Only accessible by TEACHER and ADMINISTRATOR roles.")
    @APIResponse(responseCode = "200", description = "Category deleted successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @APIResponse(responseCode = "404", description = "Category not found")
    public Response deleteCategory(@PathParam("id") Long id) {
        service.deleteCategory(id);
        return Response.ok(ApiResponse.deleted("Success")).build();
    }
}
