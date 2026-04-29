package org.ichwan.resource;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "Endpoints for user registration, login, and token refresh.")
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
    @Operation(summary = "Test authentication", description = "A simple endpoint to test if the user is authenticated with TEACHER or ADMINISTRATOR roles.")
    @APIResponse(responseCode = "200", description = "Test success", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @APIResponse(responseCode = "401", description = "Unauthorized")
    @APIResponse(responseCode = "403", description = "Forbidden")
    public Response testAuth() {
        return Response.ok(ApiResponse.ok("test success")).build();
    }

    @POST
    @Path("/register")
    @PermitAll
    @Operation(summary = "Register a new user", description = "Allows any visitor to register as a new user in the system.")
    @APIResponse(responseCode = "201", description = "User created successfully", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @APIResponse(responseCode = "400", description = "Invalid request data")
    @APIResponse(responseCode = "409", description = "User with registration number already exists")
    public Response register(UserRequest req) {
        UserResponse userResponse = userService.create(req);
        return Response.status(Response.Status.CREATED).entity(ApiResponse.created("User Created", userResponse)).build();
    }

    @POST
    @Path("/login")
    @PermitAll
    @Operation(summary = "User login", description = "Authenticates a user and returns an access token and user information.")
    @APIResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    @APIResponse(responseCode = "401", description = "Invalid registration number or password")
    public Response login(UserRequest req) {
        UserResponse user = userService.findByRegnumber(req.regnumber());
        if (user == null || !authService.authenticate(req.password(), user.getRegnumber())) {

            return Response.status(Response.Status.UNAUTHORIZED).entity("invalid email or password").build();
        }

        return Response.ok(ApiResponse.ok("Successfully login", new AuthResponse(authService.generateAccessToken(req.regnumber()), user))).build();
    }

    @POST
    @Path("/refresh")
    @Operation(summary = "Refresh access token", description = "Refreshes an expired access token using a valid refresh token.")
    @APIResponse(responseCode = "200", description = "Token refreshed successfully", content = @Content(schema = @Schema(implementation = TokenResponse.class)))
    @APIResponse(responseCode = "400", description = "Refresh token is required")
    @APIResponse(responseCode = "401", description = "Invalid or expired refresh token")
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
