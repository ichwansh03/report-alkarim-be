package org.ichwan.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.dto.CategoryRequest;
import org.ichwan.service.impl.CategoryServiceImpl;

@Path("/category")
@Produces("application/json")
@Consumes("application/json")
public class CategoryResource {

    @Inject
    private CategoryServiceImpl service;

    @GET
    public Response getAllCategories() {
        return Response.ok(service.getAllCategories()).build();
    }

    // Endpoint to create a new category
     @POST
     @Path("/create")
     public Response createCategory(CategoryRequest request) {
         service.createCategory(request.name());
         return Response.status(Response.Status.CREATED).entity("Category created").build();
     }

    // Endpoint to delete a category by ID
    @DELETE
    @Path("/delete/{id}")
    public Response deleteCategory(@PathParam("id") Long id) {
        service.deleteCategory(id);
        return Response.ok("Category deleted").build();
    }
}
