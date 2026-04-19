package org.ichwan.resource;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.ichwan.domain.RefreshToken;
import org.ichwan.dto.request.TokenRequest;
import org.ichwan.dto.request.UserRequest;
import org.ichwan.dto.response.ApiResponse;
import org.ichwan.dto.response.AuthResponse;
import org.ichwan.dto.response.TokenResponse;
import org.ichwan.dto.response.UserResponse;
import org.ichwan.service.AuthService;
import org.ichwan.service.UserService;
import org.ichwan.util.RefreshTokenService;

import java.util.Optional;

@Path("/api/v1/auths")
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
    public Response register(UserRequest req) {
        UserResponse userResponse = userService.create(req);
        return Response.status(Response.Status.CREATED).entity(ApiResponse.created("User Created", userResponse)).build();
    }

    @POST
    @Path("/login")
    @PermitAll
    public Response login(UserRequest req) {
        UserResponse user = userService.findByRegnumber(req.regNumber());
        if (user == null || !authService.authenticate(req.password(), user.getRegnumber())) {

            return Response.status(Response.Status.UNAUTHORIZED).entity("invalid email or password").build();
        }

        return Response.ok(ApiResponse.ok("Successfully login", new AuthResponse(authService.generateAccessToken(req.regNumber()), user))).build();
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
