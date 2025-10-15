package org.ichwan.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class QuestionResourceTest {

    @Test
    void testCreateQuestion() {
        String json = "{" +
                "\"question\":\"What is 2+2?\"," +
                "\"options\":[\"2\",\"3\",\"4\"]," +
                "\"category\":\"Math\"," +
                "\"target\":\"Student\"}";
        given()
            .contentType(ContentType.JSON)
            .body(json)
            .when().post("/questions/create")
            .then()
            .statusCode(201)
            .body(is("Question created"));
    }

    @Test
    void testGetQuestionsByTarget() {
        given()
            .when().get("/questions/target/Student")
            .then()
            .statusCode(200);
    }

    @Test
    void testUpdateQuestionNotFound() {
        String json = "{" +
                "\"question\":\"Updated?\"," +
                "\"options\":[\"1\",\"2\"]," +
                "\"category\":\"Math\"," +
                "\"target\":\"Student\"}";
        given()
            .contentType(ContentType.JSON)
            .body(json)
            .when().put("/questions/update/9999")
            .then()
            .statusCode(404)
            .body(is("Question not found"));
    }

    @Test
    void testDeleteQuestion() {
        given()
            .when().delete("/questions/delete/9999")
            .then()
            .statusCode(200)
            .body(is("Question deleted"));
    }
}

