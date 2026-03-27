package org.ichwan.resource;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.domain.RefreshToken;
import org.ichwan.dto.*;
import org.ichwan.service.AuthService;
import org.ichwan.service.UserService;
import org.ichwan.util.RefreshTokenService;

import java.util.Optional;

@Path("/auth")
@Consumes("application/json")
@Produces("application/json")
public class AuthResource {

    @Inject
    private AuthService authService;

    @Inject
    private UserService userService;

    @Inject
    private RefreshTokenService tokenService;

    @GET
    @Path("/test")
    @RolesAllowed({"TEACHER","ADMINISTRATOR"})
    public Response testAuth() {
        return Response.ok(ApiResponse.ok("test success")).build();
    }

    @POST
    @Path("/register")
    @PermitAll
    public Response register(AuthRequest req) {
        authService.register(req);
        return Response.status(Response.Status.CREATED).entity(ApiResponse.created("User Created", req)).build();
    }

    @POST
    @Path("/login")
    @PermitAll
    public Response login(AuthRequest req) {
        UserResponse user = userService.findByRegnumber(req.regnumber());
        if (user == null || !authService.authenticate(req.password(), user.getRegnumber())) {

            return Response.status(Response.Status.UNAUTHORIZED).entity("invalid email or password").build();
        }

        return Response.ok(ApiResponse.ok("Successfully login", new AuthResponse(req.regnumber(), authService.generateAccessToken(req.regnumber()), user))).build();
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
        UserResponse byId = userService.findById(oldRt.getUserId());
        String newAccessToken = tokenService.generateNewToken(byId.getRegnumber());

        RefreshToken newRt = tokenService.changeToken(oldRt);

        return Response.ok(
                new TokenResponse(newAccessToken, newRt.getToken())
        ).build();
    }

}
