package com.project.Task.tracer.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI();

        openAPI.setInfo(new Info()
                .title("API Documentation")
                .version("1.0.0")
                .description("API documentation for task tracer.")
        );
        openAPI.addSecurityItem(new SecurityRequirement().addList("BearerAuth"));
        openAPI.components(new Components().addSecuritySchemes("BearerAuth",
                new SecurityScheme().type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")));

        return openAPI;
    }
}
