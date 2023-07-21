package com.javaspring.blogapi.config.swagger;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@SecurityScheme(
        name = "Authorization",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Thiết lập các server dùng để test api
                .servers(Lists.newArrayList(new Server().url("http://ec2-52-221-242-149.ap-southeast-1.compute.amazonaws.com:8080"), new Server().url("http://localhost:8080")))
                // info
                .info(new Info().title("API - News application")
                        .description("OpenAPI 3.0")
                        .contact(new Contact()
                                .email("goldenv@gmail.com")
                                .name("Vo~"))
                        .version("1.0.0"));
    }
}
