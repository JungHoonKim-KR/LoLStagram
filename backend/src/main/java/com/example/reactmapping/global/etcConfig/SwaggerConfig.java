package com.example.reactmapping.global.etcConfig;

import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/auth/**")
                .addOpenApiCustomizer(apiKeyOpenApiCustomiser())
                .build();
    }

    @Bean
    public GroupedOpenApi privateApi() {
        return GroupedOpenApi.builder()
                .group("private")
                .pathsToMatch("/post/**","/update/**")
                .addOpenApiCustomizer(apiKeyOpenApiCustomiser())
                .build();
    }

    private OpenApiCustomizer apiKeyOpenApiCustomiser() {
        return openApi -> {
            SecurityScheme securityScheme = new SecurityScheme().type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .in(SecurityScheme.In.HEADER)
                    .name("Authorization");
            openApi.getComponents().addSecuritySchemes("Authorization", securityScheme);

            SecurityRequirement securityItem = new SecurityRequirement().addList("Authorization");
            openApi.addSecurityItem(securityItem);
        };
    }
}