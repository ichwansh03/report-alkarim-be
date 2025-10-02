package org.ichwan.resource;

import io.quarkus.logging.Log;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.ichwan.domain.User;
import org.ichwan.dto.AuthRequest;
import org.ichwan.dto.AuthResponse;
import org.ichwan.service.impl.UserService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Path("/auth")
public class AuthResource {

    @Inject
    private UserService userService;

    @POST
    @Path("/register")
    @Consumes("application/json")
    public Response register(AuthRequest req) {
        if (req.email() == null || req.password() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("email and password required").build();
        }

        User u = new User();
        u.setName(req.name());
        u.setEmail(req.email());
        u.setClsroom(req.clsroom());
        u.setGender(req.gender());
        u.setRoles(req.roles());
        u.setPassword(req.password());

        userService.register(u);
        return Response.status(Response.Status.CREATED).entity("user created").build();
    }

    @POST
    @Path("/login")
    @Consumes("application/json")
    public Response login(AuthRequest req) {
        User user = userService.findByEmail(req.email());
        if (user == null || !userService.authenticate(req.password(), user.getPassword())) {

            return Response.status(Response.Status.UNAUTHORIZED).entity("invalid email or password").build();
        }

        String token = Jwt.issuer("report-alkarim-issuer")
                .subject(String.valueOf(user.getId()))
                .upn(user.getName())
                .groups(user.getRoles())
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .sign();

        return Response.ok(new AuthResponse(token)).build();
    }
}
