package com.blogapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;

@Configuration
public class SwaggerOpenApiConfiguration {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("Blog app v2")
                        .description("Api for blogging application")
                        .version("1.0")
                        .contact(new Contact().name("Spring boot").email("www.spring.io")
                                .url("localhost.com"))
                        .license(new License().name("License of API")
                                .url("API license URL")));
    }

    private io.swagger.v3.oas.models.security.SecurityScheme createAPIKeyScheme() {
        return new io.swagger.v3.oas.models.security.SecurityScheme().bearerFormat("JWT").scheme("bearer")
                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP);
    }

}
