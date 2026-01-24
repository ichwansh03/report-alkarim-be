package org.ichwan.resource;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.domain.RefreshToken;
import org.ichwan.domain.User;
import org.ichwan.dto.*;
import org.ichwan.service.impl.AuthServiceImpl;
import org.ichwan.service.impl.UserServiceImpl;
import org.ichwan.util.RefreshTokenService;

import java.util.Optional;

@Path("/auth")
@Consumes("application/json")
@Produces("application/json")
public class AuthResource {

    @Inject
    private AuthServiceImpl authService;

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

        authService.register(u);
        return Response.status(Response.Status.CREATED).entity("user created").build();
    }

    @POST
    @Path("/login")
    @PermitAll
    public Response login(AuthRequest req) {
        UserResponse user = userService.findByRegnumber(req.regnumber());
        if (user == null || !authService.authenticate(req.password(), user.regnumber())) {

            return Response.status(Response.Status.UNAUTHORIZED).entity("invalid email or password").build();
        }

        return Response.ok(new AuthResponse(req.regnumber(), authService.generateAccessToken(user), user)).build();
    }

    @POST
    @Path("/refresh")
    public Response refreshToken(TokenRequest request) {
        if (request.refreshToken() == null || request.refreshToken().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("refreshToken is required")
                    .build();
        }

        Optional<RefreshToken> storedRt = tokenService.validateRefreshToken(request.refreshToken());

        if (storedRt.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid or expired refresh token")
                    .build();
        }

        RefreshToken oldRt = storedRt.get();
        String newAccessToken = tokenService.generateNewToken(oldRt.getUserId());

        RefreshToken newRt = tokenService.changeToken(oldRt);

        return Response.ok(
                new TokenResponse(newAccessToken, newRt.getToken())
        ).build();
    }

}
