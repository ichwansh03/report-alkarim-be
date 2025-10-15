package org.ichwan.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class ReportResourceTest {

    @Test
    void testCreateReport() {
        String json = "{" +
                "\"category\":\"Math\"," +
                "\"content\":\"Test content\"," +
                "\"answer\":\"A\"," +
                "\"regnumber\":\"123456\"," +
                "\"score\":\"95\"}";
        given()
            .contentType(ContentType.JSON)
            .body(json)
            .when().post("/reports/create")
            .then()
            .statusCode(201)
            .body(is("report created"));
    }

    @Test
    void testGetReportsByRegnumber() {
        given()
            .when().get("/reports/regnumber/123456")
            .then()
            .statusCode(200);
    }

    @Test
    void testUpdateReportNotFound() {
        String json = "{" +
                "\"category\":\"Math\"," +
                "\"content\":\"Updated content\"," +
                "\"answer\":\"B\"," +
                "\"regnumber\":\"123456\"," +
                "\"score\":\"90\"}";
        given()
            .contentType(ContentType.JSON)
            .body(json)
            .when().put("/reports/update/9999")
            .then()
            .statusCode(404)
            .body(is("report not found"));
    }

    @Test
    void testDeleteReportNotFound() {
        given()
            .when().delete("/reports/delete/9999")
            .then()
            .statusCode(404)
            .body(is("report not found"));
    }
}

