package org.ichwan.exceptions;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.ichwan.dto.ErrorResponse;

public class AppExceptionMapper implements ExceptionMapper<AppException> {

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(AppException ex) {
        return Response
                .status(ex.getStatusCode())
                .entity(new ErrorResponse(
                        ex.getStatusCode(),
                        ex.getError(),
                        ex.getMessage(),
                        uriInfo.getPath()
                ))
                .type(MediaType.APPLICATION_JSON)
                .build();

    }
}
