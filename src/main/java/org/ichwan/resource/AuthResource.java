package org.ichwan.resource;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.domain.RefreshToken;
import org.ichwan.domain.User;
import org.ichwan.dto.AuthRequest;
import org.ichwan.dto.AuthResponse;
import org.ichwan.dto.TokenRequest;
import org.ichwan.dto.TokenResponse;
import org.ichwan.util.RefreshTokenService;
import org.ichwan.service.impl.UserServiceImpl;

import java.util.Optional;

@Path("/auth")
@Consumes("application/json")
@Produces("application/json")
public class AuthResource {

    @Inject
    private UserServiceImpl userService;
    @Inject
    private RefreshTokenService tokenService;

    @GET
    @Path("/test")
    @RolesAllowed({"TEACHER","ADMINISTRATOR"})
    public Response testAuth() {
        return Response.ok("Authentication successful").build();
    }

    @POST
    @Path("/register")
    @PermitAll
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
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
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
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getUsersByClassAndRoles(@PathParam("class") String clsroom, @PathParam("roles") String roles) {
        return Response.ok(userService.findByClsroomAndRoles(clsroom, roles)).build();
    }

    @GET
    @Path("/user/{regnumber}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getUserByRegnumber(@PathParam("regnumber") String regnumber) {
        User user = userService.findByRegnumber(regnumber);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
        return Response.ok(user).build();
    }

    @GET
    @Path("/roles/{roles}")
    @RolesAllowed({"TEACHER","ADMINISTRATOR","STUDENT"})
    public Response getUsersByRoles(@PathParam("roles") String roles) {
        return Response.ok(userService.findByRoles(roles)).build();
    }

    @DELETE
    @Path("/user/delete/{id}")
    @RolesAllowed("ADMINISTRATOR")
    public Response deleteUser(@PathParam("id") Long id) {
        userService.deleteUser(id);
        return Response.ok("user deleted").build();
    }

    @POST
    @Path("/login")
    @PermitAll
    public Response login(AuthRequest req) {
        User user = userService.findByRegnumber(req.regnumber());
        if (user == null || !userService.authenticate(req.password(), user.getPassword())) {

            return Response.status(Response.Status.UNAUTHORIZED).entity("invalid email or password").build();
        }

        return Response.ok(new AuthResponse(req.regnumber(), userService.generateAccessToken(user))).build();
    }

    @POST
    @Path("/refresh")
    public Response refreshToken(TokenRequest request) {

        // Validasi ada input atau tidak
        if (request.refreshToken() == null || request.refreshToken().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("refreshToken is required")
                    .build();
        }

        // Cari refresh token di DB
        Optional<RefreshToken> storedRt = tokenService.validateRefreshToken(request.refreshToken());

        if (storedRt.isEmpty()) {
            // refresh token tidak valid / expired / tidak ditemukan
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid or expired refresh token")
                    .build();
        }

        RefreshToken oldRt = storedRt.get();
        String newAccessToken = tokenService.generateNewToken(oldRt.getUserId());

        // Rotasi refresh token (hapus lama → buat baru)
        RefreshToken newRt = tokenService.createRefreshToken(oldRt.getUserId());

        // Return token baru
        return Response.ok(
                new TokenResponse(newAccessToken, newRt.getToken())
        ).build();
    }

}
