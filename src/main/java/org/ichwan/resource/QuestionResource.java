package org.ichwan.resource;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.dto.request.QuestionRequest;
import org.ichwan.service.QuestionService;

@Path("/questions")
@Consumes("application/json")
@Produces("application/json")
public class QuestionResource {

    @Inject
    private QuestionService service;

    @POST
    @Path("/create")
    @RolesAllowed({"TEACHER","ADMINISTRATOR"})
    public Response createQuestion(QuestionRequest question) {
        service.createQuestion(question);

        return Response.status(Response.Status.CREATED).entity("Question created").build();
    }

    @GET
    @Path("/target/{target}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getQuestionsByTarget(@PathParam("target") String target) {
        return Response.ok(service.getQuestionByTarget(target)).build();
    }

    @GET
    @Path("/category/{category}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getQuestionsByCategory(@PathParam("category") String category) {
        return Response.ok(service.getQuestionByCategory(category)).build();
    }

    @GET
    @Path("/category/{category}/target/{target}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getQuestionsByCategoryAndTarget(@PathParam("category") String category, @PathParam("target") String target) {
        return Response.ok(service.getQuestionByCategoryAndTarget(category, target)).build();
    }

    @PUT
    @Path("/update/{id}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response updateQuestion(@PathParam("id") Long id, QuestionRequest question) {
        service.updateQuestion(question, id);
        return Response.ok("Question updated").build();
    }

    @DELETE
    @Path("/delete/{id}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR"})
    public Response deleteQuestion(@PathParam("id") Long id) {
        service.deleteQuestion(id);
        return Response.ok("Question deleted").build();
    }

    @GET
    @RolesAllowed("ADMINISTRATOR")
    public Response getAllQuestions(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        return Response.ok(service.getAll(page, size)).build();
    }
}
