package org.ichwan.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.dto.request.QuestionRequest;
import org.ichwan.dto.response.ApiResponse;
import org.ichwan.dto.response.QuestionResponse;
import org.ichwan.service.QuestionService;

@Path("/api/v1/questions")
@Consumes("application/json")
@Produces("application/json")
public class QuestionResource {

    @Inject
    private QuestionService service;

    @POST
    @Path("/create")
    @RolesAllowed({"TEACHER","ADMINISTRATOR"})
    public Response createQuestion(QuestionRequest question) {
        QuestionResponse response = service.create(question);

        return Response.ok(ApiResponse.created("Success", response)).build();
    }

    @GET
    @Path("/target/{target}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getQuestionsByTarget(@PathParam("target") String target) {
        return Response.ok(ApiResponse.ok("Success", service.getQuestionByTarget(target))).build();
    }

    @GET
    @Path("/category/{category}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getQuestionsByCategory(@PathParam("category") String category) {
        return Response.ok(ApiResponse.ok("Success", service.getQuestionByCategory(category))).build();
    }

    @GET
    @Path("/category/{category}/target/{target}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getQuestionsByCategoryAndTarget(@PathParam("category") String category, @PathParam("target") String target) {
        return Response.ok(ApiResponse.ok("Success", service.getQuestionByCategoryAndTarget(category, target))).build();
    }

    @PUT
    @Path("/update/{id}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response updateQuestion(@PathParam("id") Long id, QuestionRequest question) {
        QuestionResponse response = service.update(question, id);
        return Response.ok(ApiResponse.ok("Success", response)).build();
    }

    @DELETE
    @Path("/delete/{id}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR"})
    public Response deleteQuestion(@PathParam("id") Long id) {
        service.delete(id);
        return Response.ok(ApiResponse.deleted("Success")).build();
    }

    @GET
    @RolesAllowed("ADMINISTRATOR")
    public Response getAllQuestions(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        return Response.ok(ApiResponse.ok("Success", service.getAll(page, size))).build();
    }
}
