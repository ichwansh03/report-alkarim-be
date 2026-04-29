package org.ichwan.util;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;

@OpenAPIDefinition(
    info = @Info(
        title = "Students Report Al-Karim API",
        version = "1.0.0",
        description = "API for managing student reports, questions, categories, and classrooms.",
        contact = @Contact(name = "Ichwan Sholihin", email = "ichwansholihin70@gmail.com"),
        license = @License(name = "MIT", url = "https://opensource.org/licenses/MIT")
    ),
    security = @SecurityRequirement(name = "jwt")
)
@SecuritySchemes({
    @SecurityScheme(
        securitySchemeName = "jwt",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
    )
})
public class SwaggerConfig extends Application {
}
