package org.ichwan.util;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.ichwan.service.impl.RedisService;

import java.io.IOException;

@Provider
@Priority(Priorities.AUTHORIZATION) //set timing of filter, execute before processing request
public class RateLimitFilter implements ContainerRequestFilter {

    @Inject
    private RedisService redisService;
    public static final long LIMIT = 10;
    public static final long WINDOW_IN_SECONDS = 60;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        String clientIp = getClientIp(containerRequestContext);
        String path = containerRequestContext.getUriInfo().getPath();
        String method = containerRequestContext.getMethod();
        String key = clientIp + ":" + method + ":" + path;

        boolean allowed = redisService.allowed(key, LIMIT, WINDOW_IN_SECONDS);
        if (!allowed) {
            containerRequestContext.abortWith(Response
                    .status(Response.Status.TOO_MANY_REQUESTS)
                    .entity("Too many requests - Rate limit exceeded")
                    .build());
        }
    }

    private String getClientIp(ContainerRequestContext requestContext) {
        String forwarded = requestContext.getHeaderString("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return requestContext.getHeaderString("Host");
    }
}
