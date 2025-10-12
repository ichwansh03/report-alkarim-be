package org.ichwan.resource;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.domain.Question;
import org.ichwan.dto.QuestionRequest;
import org.ichwan.service.impl.QuestionServiceImpl;

@Path("/questions")
@Consumes("application/json")
@Produces("application/json")
public class QuestionResource {

    @Inject
    private QuestionServiceImpl service;

    @POST
    @Path("/create")
    public Response createQuestion(QuestionRequest question) {
        Question quest = new Question();
        quest.setQuestion(question.question());
        quest.setOptions(question.options());
        quest.setCategory(question.category());
        quest.setTarget(question.target());
        service.createQuestion(quest);

        return Response.status(Response.Status.CREATED).entity("Question created").build();
    }

    @GET
    @Path("/target/{target}")
    public Response getQuestionsByTarget(@PathParam("target") String target) {
        return Response.ok(service.getQuestionByTarget(target)).build();
    }

    @GET
    @Path("/category/{category}")
    public Response getQuestionsByCategory(@PathParam("category") String category) {
        return Response.ok(service.getQuestionByCategory(category)).build();
    }

    @PUT
    @Path("/update/{id}")
    public Response updateQuestion(@PathParam("id") Long id, QuestionRequest question) {
        Question quest = service.getQuestionById(id);
        if (quest == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Question not found").build();
        }
        quest.setQuestion(question.question());
        quest.setOptions(question.options());
        quest.setCategory(question.category());
        quest.setTarget(question.target());
        service.updateQuestion(quest, id);
        return Response.ok("Question updated").build();
    }

    @DELETE
    @Path("/delete/{id}")
    public Response deleteQuestion(@PathParam("id") Long id) {
        service.deleteQuestion(id);
        return Response.ok("Question deleted").build();
    }
}
