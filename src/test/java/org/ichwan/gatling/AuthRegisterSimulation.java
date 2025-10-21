package org.ichwan.gatling;

import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.ThreadLocalRandom;

public class AuthRegisterSimulation extends Simulation {

    private static final String BASE_URL = "http://localhost:8080";

    private static final HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private static String jsonRepeated() {
        int id = ThreadLocalRandom.current().nextInt(10000, 99999);
        return String.format(
                "{\"name\":\"User%d\",\"regnumber\":\"reg%d\",\"clsroom\":\"A\",\"gender\":\"M\",\"roles\":\"STUDENT\",\"password\":\"pass%d\"}",
                id, id, id
        );
    }

    private static final ScenarioBuilder registerUser = scenario("Register Users")
            .repeat(50).on(exec(
                http("Register User")
                    .post("/auth/register")
                        .body(StringBody(session -> jsonRepeated()))
                        .check(status().is(201))
            ));

    private static final ScenarioBuilder updateUser = scenario("Update Users")
            .repeat(50).on(exec(
                http("Update User")
                    .put(session -> {
                        int userId = ThreadLocalRandom.current().nextInt(1, 500);
                        return "/auth/update/" + userId;
                    })
                        .body(StringBody(session -> jsonRepeated()))
                        .check(status().is(200))
            ));

    {
        setUp(
            registerUser.injectOpen(atOnceUsers(10), rampUsers(50).during(30)),
            updateUser.injectOpen(atOnceUsers(10), rampUsers(50).during(30))
        ).assertions(
            forAll().responseTime().max().lt(2000),
            forAll().successfulRequests().percent().gt(95.0),
            forAll().failedRequests().count().lt(100L)
        ).protocols(httpProtocol);
    }

    @Override
    public void before() {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "12345";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("DELETE FROM users");

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next()) {
                System.out.println("User count before test: " + rs.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

