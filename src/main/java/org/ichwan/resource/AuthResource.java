package org.ichwan.resource;

import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.domain.User;
import org.ichwan.dto.AuthRequest;
import org.ichwan.dto.AuthResponse;
import org.ichwan.service.impl.RedisService;
import org.ichwan.service.impl.UserServiceImpl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Path("/auth")
@Consumes("application/json")
@Produces("application/json")
public class AuthResource {

    @Inject
    private UserServiceImpl userService;
    @Inject
    private RedisService redisService;

    @POST
    @Path("/register")
    public Response register(AuthRequest req) {
        if (req.regnumber() == null || req.password() == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("NISN/NIP dan password salah").build();
        }

        User u = new User();
        u.setName(req.name());
        u.setRegnumber(req.regnumber());
        u.setClsroom(req.clsroom());
        u.setGender(req.gender());
        u.setRoles(req.roles());
        u.setPassword(req.password());

        userService.register(u);
        return Response.status(Response.Status.CREATED).entity("user created").build();
    }

    @PUT
    @Path("/update/{id}")
    public Response update(Long id, AuthRequest req) {
        User u = userService.finById(id);
        if (u == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("User tidak ditemukan").build();
        }
        u.setName(req.name());
        u.setClsroom(req.clsroom());
        u.setGender(req.gender());
        u.setRoles(req.roles());
        u.setPassword(req.password());
        userService.update(u, id);
        return Response.ok("user updated").build();
    }

    @GET
    @Path("/class/{class}/roles/{roles}")
    public Response getUsersByClassAndRoles(@PathParam("class") String clsroom, @PathParam("roles") String roles) {
        return Response.ok(userService.findByClsroomAndRoles(clsroom, roles)).build();
    }

    @GET
    @Path("/user/{regnumber}")
    public Response getUserByRegnumber(@PathParam("regnumber") String regnumber) {
        User user = userService.findByRegnumber(regnumber);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
        return Response.ok(user).build();
    }

    @GET
    @Path("/roles/{roles}")
    public Response getUsersByRoles(@PathParam("roles") String roles) {
        return Response.ok(userService.findByRoles(roles)).build();
    }

    @DELETE
    @Path("/user/delete/{id}")
    public Response deleteUser(@PathParam("id") Long id) {
        userService.deleteUser(id);
        return Response.ok("user deleted").build();
    }

    @POST
    @Path("/login")
    public Response login(AuthRequest req) {
        User user = userService.findByRegnumber(req.regnumber());
        if (user == null || !userService.authenticate(req.password(), user.getPassword())) {

            return Response.status(Response.Status.UNAUTHORIZED).entity("invalid email or password").build();
        }

        boolean allowed = redisService.allowed(req.regnumber(), 5, 60);
        if (!allowed) {
            return Response.status(Response.Status.TOO_MANY_REQUESTS).entity("Too many login attempts. Please try again later.").build();
        }

        String token = Jwt.issuer("report-alkarim-issuer")
                .subject(String.valueOf(user.getId()))
                .upn(user.getName())
                .groups(user.getRoles().toString())
                .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                .sign();

        return Response.ok(new AuthResponse(req.regnumber(), token)).build();
    }
}
