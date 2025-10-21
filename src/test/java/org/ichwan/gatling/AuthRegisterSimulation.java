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

public class AuthRegisterSimulation extends Simulation {

    private static final String BASE_URL = "http://host.docker.internal:8080";

    private static final FeederBuilder.Batchable<String> feeder = csv("auth_users.csv").circular();

    private static final HttpProtocolBuilder httpProtocol = http
            .baseUrl(BASE_URL)
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private static final ScenarioBuilder scn = scenario("Register Users")
            .feed(feeder)
            .exec(
                http("Register User")
                    .post("/auth/register")
                        .body(StringBody(
                                "{\"name\":\"${name}\",\"regnumber\":\"${regnumber}\",\"clsroom\":\"${clsroom}\",\"gender\":\"${gender}\",\"roles\":\"${roles}\",\"password\":\"${password}\"}"
                        ))
                        .check(status().is(201))
            );

    {
        setUp(
            scn.injectOpen(
                atOnceUsers(10),
                rampUsers(50).during(30)
            )
        ).protocols(httpProtocol);
    }

    @Override
    public void before() {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "12345";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement()) {
            // Example: Clean up users table before test
            stmt.executeUpdate("DELETE FROM users");
            // Example: Check user count
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next()) {
                System.out.println("User count before test: " + rs.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
