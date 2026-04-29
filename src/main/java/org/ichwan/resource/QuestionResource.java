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
import org.ichwan.dto.request.QuestionRequest;
import org.ichwan.dto.response.ApiResponse;
import org.ichwan.dto.response.QuestionResponse;
import org.ichwan.service.QuestionService;

@Path("/api/v1/questions")
@Consumes("application/json")
@Produces("application/json")
@Tag(name = "Question", description = "Endpoints for managing questions.")
public class QuestionResource {

    @Inject
    private QuestionService service;

    @POST
    @Path("/create")
    @RolesAllowed({"TEACHER","ADMINISTRATOR"})
    @Operation(summary = "Create a new question", description = "Creates a new question for students. Only accessible by TEACHER and ADMINISTRATOR roles.")
    @APIResponse(responseCode = "201", description = "Question created successfully")
    @APIResponse(responseCode = "409", description = "Question with the same name already exists")
    public Response createQuestion(QuestionRequest question) {
        QuestionResponse response = service.create(question);

        return Response.ok(ApiResponse.created("Success", response)).build();
    }

    @GET
    @Path("/target/{target}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    @Operation(summary = "Get all questions by target", description = "Returns a list of all questions based on target.")
    @APIResponse(responseCode = "200", description = "Successfully retrieved questions", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public Response getQuestionsByTarget(@PathParam("target") String target) {
        return Response.ok(ApiResponse.ok("Success", service.getQuestionByTarget(target))).build();
    }

    @GET
    @Path("/category/{category}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    @Operation(summary = "Get all questions by category", description = "Returns a list of all questions based on category.")
    @APIResponse(responseCode = "200", description = "Successfully retrieved questions", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public Response getQuestionsByCategory(@PathParam("category") String category) {
        return Response.ok(ApiResponse.ok("Success", service.getQuestionByCategory(category))).build();
    }

    @GET
    @Path("/category/{category}/target/{target}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    @Operation(summary = "Get all questions by target and category", description = "Returns a list of all questions based on target and category.")
    @APIResponse(responseCode = "200", description = "Successfully retrieved questions", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public Response getQuestionsByCategoryAndTarget(@PathParam("category") String category, @PathParam("target") String target) {
        return Response.ok(ApiResponse.ok("Success", service.getQuestionByCategoryAndTarget(category, target))).build();
    }

    @PUT
    @Path("/update/{id}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    @Operation(summary = "Update a question", description = "Updates an existing question by its ID. Only accessible by TEACHER, ADMINISTRATOR, and STUDENT roles.")
    @APIResponse(responseCode = "200", description = "Question updated successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @APIResponse(responseCode = "404", description = "Question not found")
    public Response updateQuestion(@PathParam("id") Long id, QuestionRequest question) {
        QuestionResponse response = service.update(question, id);
        return Response.ok(ApiResponse.ok("Success", response)).build();
    }

    @DELETE
    @Path("/delete/{id}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR"})
    @Operation(summary = "Delete a question", description = "Deletes an existing question by its ID. Only accessible by TEACHER and ADMINISTRATOR roles.")
    @APIResponse(responseCode = "200", description = "Question deleted successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @APIResponse(responseCode = "404", description = "Question not found")
    public Response deleteQuestion(@PathParam("id") Long id) {
        service.delete(id);
        return Response.ok(ApiResponse.deleted("Success")).build();
    }

    @GET
    @RolesAllowed("ADMINISTRATOR")
    @Operation(summary = "Get all questions", description = "Returns a list of all questions.")
    @APIResponse(responseCode = "200", description = "Successfully retrieved questions", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    public Response getAllQuestions(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("10") int size
    ) {
        return Response.ok(ApiResponse.ok("Success", service.getAll(page, size))).build();
    }
}
