package org.ichwan.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class CategoryResourceTest {

    @Test
    void testGetAllCategories() {
        given()
            .when().get("/category")
            .then()
            .statusCode(200);
    }

    @Test
    void testCreateCategory() {
        String json = "{\"name\":\"Science\"}";
        given()
            .contentType(ContentType.JSON)
            .body(json)
            .when().post("/category/create")
            .then()
            .statusCode(201)
            .body(is("Category created"));
    }

    @Test
    void testDeleteCategory() {
        given()
            .when().delete("/category/delete/9999")
            .then()
            .statusCode(200)
            .body(is("Category deleted"));
    }
}

